package com.shopsphere.controller;

import com.shopsphere.dao.AddressDAO;
import com.shopsphere.model.Address;
import com.shopsphere.model.CartItem;
import com.shopsphere.model.Coupon;
import com.shopsphere.model.Order;
import com.shopsphere.model.User;
import com.shopsphere.service.CartService;
import com.shopsphere.service.CheckoutService;
import com.shopsphere.util.CsrfUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Controller for Checkout process and transactional Order Placement.
 */
@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout", "/place-order"})
public class CheckoutServlet extends HttpServlet {

    private final CartService cartService = new CartService();
    private final AddressDAO addressDAO = new AddressDAO();
    private final CheckoutService checkoutService = new CheckoutService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?error=auth_required");
            return;
        }

        List<CartItem> cartItems = cartService.getCartForUser(user.getUserId());
        if (cartItems.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        BigDecimal subtotal = cartService.calculateSubtotal(cartItems);
        Coupon appliedCoupon = (Coupon) session.getAttribute("appliedCoupon");
        BigDecimal discount = BigDecimal.ZERO;
        if (appliedCoupon != null) {
            discount = appliedCoupon.calculateDiscount(subtotal);
        }

        BigDecimal taxableAmount = subtotal.subtract(discount);
        if (taxableAmount.compareTo(BigDecimal.ZERO) < 0) {
            taxableAmount = BigDecimal.ZERO;
        }

        BigDecimal tax = taxableAmount.multiply(BigDecimal.valueOf(0.18)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal shipping = (subtotal.compareTo(BigDecimal.valueOf(1000)) >= 0)
                ? BigDecimal.ZERO : BigDecimal.valueOf(99.00);
        BigDecimal grandTotal = taxableAmount.add(tax).add(shipping).setScale(2, RoundingMode.HALF_UP);

        List<Address> addresses = addressDAO.findByUserId(user.getUserId());

        req.setAttribute("cartItems", cartItems);
        req.setAttribute("addresses", addresses);
        req.setAttribute("subtotal", subtotal);
        req.setAttribute("discount", discount);
        req.setAttribute("tax", tax);
        req.setAttribute("shipping", shipping);
        req.setAttribute("grandTotal", grandTotal);
        req.setAttribute("appliedCoupon", appliedCoupon);

        CsrfUtil.getOrCreateToken(session);
        req.getRequestDispatcher("/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?error=auth_required");
            return;
        }

        if (!CsrfUtil.isValidToken(req)) {
            session.setAttribute("flashError", "Invalid security token. Please try submitting again.");
            resp.sendRedirect(req.getContextPath() + "/checkout");
            return;
        }

        try {
            // Determine delivery address
            Address shippingAddress = null;
            String addressOption = req.getParameter("addressOption");

            if ("new".equalsIgnoreCase(addressOption)) {
                String recipientName = req.getParameter("newRecipientName");
                String phone = req.getParameter("newPhone");
                String addressLine1 = req.getParameter("newAddressLine1");
                String addressLine2 = req.getParameter("newAddressLine2");
                String city = req.getParameter("newCity");
                String state = req.getParameter("newState");
                String postalCode = req.getParameter("newPostalCode");
                boolean saveToBook = "true".equalsIgnoreCase(req.getParameter("saveAddress"));

                shippingAddress = new Address();
                shippingAddress.setUserId(user.getUserId());
                shippingAddress.setRecipientName(recipientName);
                shippingAddress.setPhone(phone);
                shippingAddress.setAddressLine1(addressLine1);
                shippingAddress.setAddressLine2(addressLine2);
                shippingAddress.setCity(city);
                shippingAddress.setState(state);
                shippingAddress.setPostalCode(postalCode);
                shippingAddress.setCountry("India");

                if (saveToBook) {
                    shippingAddress = addressDAO.create(shippingAddress);
                }
            } else {
                String addressIdStr = req.getParameter("addressId");
                if (addressIdStr != null && !addressIdStr.isEmpty()) {
                    int addressId = Integer.parseInt(addressIdStr);
                    Optional<Address> addrOpt = addressDAO.findById(addressId);
                    if (addrOpt.isPresent() && addrOpt.get().getUserId() == user.getUserId()) {
                        shippingAddress = addrOpt.get();
                    }
                }
            }

            if (shippingAddress == null) {
                session.setAttribute("flashError", "Please select or provide a valid shipping address.");
                resp.sendRedirect(req.getContextPath() + "/checkout");
                return;
            }

            String paymentMethod = req.getParameter("paymentMethod");
            if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
                paymentMethod = "Cash on Delivery (COD)";
            }

            Coupon appliedCoupon = (Coupon) session.getAttribute("appliedCoupon");
            String couponCode = (appliedCoupon != null) ? appliedCoupon.getCode() : null;
            String notes = req.getParameter("notes");

            // Execute transactional checkout
            Order placedOrder = checkoutService.placeOrder(user.getUserId(), shippingAddress, paymentMethod, couponCode, notes);

            // Clean up session coupon
            session.removeAttribute("appliedCoupon");

            // Redirect to Order Success page
            resp.sendRedirect(req.getContextPath() + "/order-success?orderId=" + placedOrder.getOrderId());

        } catch (Exception e) {
            session.setAttribute("flashError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/checkout");
        }
    }
}
