package com.shopsphere.controller;

import com.google.gson.JsonObject;
import com.shopsphere.model.CartItem;
import com.shopsphere.model.Coupon;
import com.shopsphere.model.Product;
import com.shopsphere.model.User;
import com.shopsphere.service.CartService;
import com.shopsphere.service.CouponService;
import com.shopsphere.service.ProductService;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Controller for Shopping Cart management and Coupon application.
 */
@WebServlet(name = "CartServlet", urlPatterns = {"/cart", "/add-to-cart", "/update-cart", "/remove-from-cart"})
public class CartServlet extends HttpServlet {

    private final CartService cartService = new CartService();
    private final ProductService productService = new ProductService();
    private final CouponService couponService = new CouponService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        User user = (User) session.getAttribute("currentUser");

        List<CartItem> cartItems = getEffectiveCart(session, user);
        BigDecimal subtotal = cartService.calculateSubtotal(cartItems);

        // Check applied coupon
        Coupon appliedCoupon = (Coupon) session.getAttribute("appliedCoupon");
        BigDecimal discount = BigDecimal.ZERO;
        if (appliedCoupon != null) {
            if (appliedCoupon.isValid() && (appliedCoupon.getMinOrderValue() == null || subtotal.compareTo(appliedCoupon.getMinOrderValue()) >= 0)) {
                discount = appliedCoupon.calculateDiscount(subtotal);
            } else {
                session.removeAttribute("appliedCoupon");
                appliedCoupon = null;
            }
        }

        BigDecimal taxableAmount = subtotal.subtract(discount);
        if (taxableAmount.compareTo(BigDecimal.ZERO) < 0) {
            taxableAmount = BigDecimal.ZERO;
        }

        BigDecimal tax = taxableAmount.multiply(BigDecimal.valueOf(0.18)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal shipping = (subtotal.compareTo(BigDecimal.valueOf(1000)) >= 0 || subtotal.compareTo(BigDecimal.ZERO) == 0)
                ? BigDecimal.ZERO : BigDecimal.valueOf(99.00);
        BigDecimal grandTotal = taxableAmount.add(tax).add(shipping).setScale(2, RoundingMode.HALF_UP);

        req.setAttribute("cartItems", cartItems);
        req.setAttribute("subtotal", subtotal);
        req.setAttribute("discount", discount);
        req.setAttribute("tax", tax);
        req.setAttribute("shipping", shipping);
        req.setAttribute("grandTotal", grandTotal);
        req.setAttribute("appliedCoupon", appliedCoupon);

        CsrfUtil.getOrCreateToken(session);
        req.getRequestDispatcher("/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        User user = (User) session.getAttribute("currentUser");
        String uri = req.getRequestURI();
        String action = req.getParameter("action");

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"));

        try {
            if (uri.endsWith("/add-to-cart") || "add".equalsIgnoreCase(action)) {
                handleAdd(req, resp, session, user, isAjax);
                return;
            } else if (uri.endsWith("/update-cart") || "update".equalsIgnoreCase(action)) {
                handleUpdate(req, resp, session, user);
                return;
            } else if (uri.endsWith("/remove-from-cart") || "remove".equalsIgnoreCase(action)) {
                handleRemove(req, resp, session, user);
                return;
            } else if ("applyCoupon".equalsIgnoreCase(action)) {
                handleApplyCoupon(req, resp, session, user);
                return;
            } else if ("removeCoupon".equalsIgnoreCase(action)) {
                session.removeAttribute("appliedCoupon");
                session.setAttribute("flashSuccess", "Coupon removed.");
            }
        } catch (Exception e) {
            session.setAttribute("flashError", e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp,
                           HttpSession session, User user, boolean isAjax) throws IOException {
        String prodIdStr = req.getParameter("productId");
        String qtyStr = req.getParameter("quantity");
        int productId = Integer.parseInt(prodIdStr);
        int quantity = (qtyStr != null && !qtyStr.trim().isEmpty()) ? Integer.parseInt(qtyStr) : 1;

        if (user != null) {
            cartService.addToCart(user.getUserId(), productId, quantity);
        } else {
            // Guest session cart
            List<CartItem> sessionCart = getSessionCart(session);
            boolean found = false;
            for (CartItem item : sessionCart) {
                if (item.getProductId() == productId) {
                    item.setQuantity(item.getQuantity() + quantity);
                    found = true;
                    break;
                }
            }
            if (!found) {
                Optional<Product> pOpt = productService.getProductById(productId);
                if (pOpt.isPresent()) {
                    CartItem newItem = new CartItem();
                    newItem.setProductId(productId);
                    newItem.setQuantity(quantity);
                    newItem.setProduct(pOpt.get());
                    sessionCart.add(newItem);
                }
            }
        }

        if (isAjax) {
            resp.setContentType("application/json");
            JsonObject json = new JsonObject();
            json.addProperty("status", "success");
            json.addProperty("message", "Item added to cart!");
            json.addProperty("cartCount", getCartTotalCount(session, user));
            resp.getWriter().write(json.toString());
            return;
        }

        session.setAttribute("flashSuccess", "Item successfully added to your cart!");
        String redirect = req.getParameter("redirect");
        if (redirect != null && !redirect.trim().isEmpty()) {
            resp.sendRedirect(redirect);
        } else {
            resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp, HttpSession session, User user) {
        int productId = Integer.parseInt(req.getParameter("productId"));
        int quantity = Integer.parseInt(req.getParameter("quantity"));

        if (user != null) {
            cartService.updateQuantity(user.getUserId(), productId, quantity);
        } else {
            List<CartItem> sessionCart = getSessionCart(session);
            if (quantity <= 0) {
                sessionCart.removeIf(item -> item.getProductId() == productId);
            } else {
                for (CartItem item : sessionCart) {
                    if (item.getProductId() == productId) {
                        item.setQuantity(quantity);
                        break;
                    }
                }
            }
        }
    }

    private void handleRemove(HttpServletRequest req, HttpServletResponse resp, HttpSession session, User user) {
        int productId = Integer.parseInt(req.getParameter("productId"));
        if (user != null) {
            cartService.removeFromCart(user.getUserId(), productId);
        } else {
            List<CartItem> sessionCart = getSessionCart(session);
            sessionCart.removeIf(item -> item.getProductId() == productId);
        }
        session.setAttribute("flashSuccess", "Item removed from cart.");
    }

    private void handleApplyCoupon(HttpServletRequest req, HttpServletResponse resp, HttpSession session, User user) {
        String code = req.getParameter("couponCode");
        List<CartItem> items = getEffectiveCart(session, user);
        BigDecimal subtotal = cartService.calculateSubtotal(items);

        try {
            Optional<Coupon> couponOpt = couponService.validateCoupon(code, subtotal);
            if (couponOpt.isPresent()) {
                session.setAttribute("appliedCoupon", couponOpt.get());
                session.setAttribute("flashSuccess", "Coupon '" + couponOpt.get().getCode() + "' applied successfully!");
            }
        } catch (IllegalArgumentException e) {
            session.setAttribute("flashError", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> getSessionCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("SESSION_CART");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("SESSION_CART", cart);
        }
        return cart;
    }

    private List<CartItem> getEffectiveCart(HttpSession session, User user) {
        if (user != null) {
            // If session cart has items from before login, merge them into DB cart
            List<CartItem> sessionCart = getSessionCart(session);
            if (!sessionCart.isEmpty()) {
                for (CartItem si : sessionCart) {
                    try {
                        cartService.addToCart(user.getUserId(), si.getProductId(), si.getQuantity());
                    } catch (Exception ignored) {
                    }
                }
                sessionCart.clear();
            }
            return cartService.getCartForUser(user.getUserId());
        }
        return getSessionCart(session);
    }

    private int getCartTotalCount(HttpSession session, User user) {
        if (user != null) {
            return cartService.getCartCount(user.getUserId());
        }
        int total = 0;
        List<CartItem> sessionCart = getSessionCart(session);
        for (CartItem item : sessionCart) {
            total += item.getQuantity();
        }
        return total;
    }
}
