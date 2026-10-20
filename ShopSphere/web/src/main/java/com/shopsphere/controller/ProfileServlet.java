package com.shopsphere.controller;

import com.shopsphere.dao.AddressDAO;
import com.shopsphere.dao.OrderDAO;
import com.shopsphere.model.Address;
import com.shopsphere.model.Order;
import com.shopsphere.model.User;
import com.shopsphere.service.UserService;
import com.shopsphere.util.CsrfUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller for Customer Profile, Address book, and Password changes.
 */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final AddressDAO addressDAO = new AddressDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?error=auth_required");
            return;
        }

        // Refresh user info
        userService.findById(user.getUserId()).ifPresent(u -> {
            session.setAttribute("currentUser", u);
            req.setAttribute("user", u);
        });

        // Load addresses and recent orders
        List<Address> addresses = addressDAO.findByUserId(user.getUserId());
        List<Order> orders = orderDAO.findByUserId(user.getUserId());

        req.setAttribute("addresses", addresses);
        req.setAttribute("orders", orders);
        CsrfUtil.getOrCreateToken(session);

        req.getRequestDispatcher("/profile.jsp").forward(req, resp);
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
            session.setAttribute("flashError", "Invalid security token.");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) {
            action = "";
        }

        try {
            switch (action) {
                case "updateProfile":
                    handleUpdateProfile(req, session, user);
                    break;
                case "changePassword":
                    handleChangePassword(req, session, user);
                    break;
                case "addAddress":
                    handleAddAddress(req, session, user);
                    break;
                case "deleteAddress":
                    handleDeleteAddress(req, session, user);
                    break;
                default:
                    session.setAttribute("flashError", "Unknown profile action.");
                    break;
            }
        } catch (Exception e) {
            session.setAttribute("flashError", e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/profile");
    }

    private void handleUpdateProfile(HttpServletRequest req, HttpSession session, User user) {
        String name = req.getParameter("name");
        String phone = req.getParameter("phone");

        if (name == null || name.trim().isEmpty()) {
            session.setAttribute("flashError", "Name cannot be empty.");
            return;
        }

        boolean updated = userService.updateProfile(user.getUserId(), name, phone);
        if (updated) {
            user.setName(name.trim());
            user.setPhone(phone != null ? phone.trim() : null);
            session.setAttribute("currentUser", user);
            session.setAttribute("flashSuccess", "Profile updated successfully!");
        } else {
            session.setAttribute("flashError", "Could not update profile.");
        }
    }

    private void handleChangePassword(HttpServletRequest req, HttpSession session, User user) {
        String oldPassword = req.getParameter("oldPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        if (newPassword == null || !newPassword.equals(confirmPassword)) {
            session.setAttribute("flashError", "New passwords do not match.");
            return;
        }

        try {
            boolean changed = userService.changePassword(user.getUserId(), oldPassword, newPassword);
            if (changed) {
                session.setAttribute("flashSuccess", "Password changed successfully!");
            } else {
                session.setAttribute("flashError", "Could not change password.");
            }
        } catch (IllegalArgumentException e) {
            session.setAttribute("flashError", e.getMessage());
        }
    }

    private void handleAddAddress(HttpServletRequest req, HttpSession session, User user) throws Exception {
        String recipientName = req.getParameter("recipientName");
        String phone = req.getParameter("phone");
        String addressLine1 = req.getParameter("addressLine1");
        String addressLine2 = req.getParameter("addressLine2");
        String city = req.getParameter("city");
        String state = req.getParameter("state");
        String postalCode = req.getParameter("postalCode");
        boolean isDefault = "true".equalsIgnoreCase(req.getParameter("isDefault"));

        if (recipientName == null || phone == null || addressLine1 == null || city == null || state == null || postalCode == null) {
            session.setAttribute("flashError", "Please fill all required address fields.");
            return;
        }

        Address address = new Address();
        address.setUserId(user.getUserId());
        address.setRecipientName(recipientName.trim());
        address.setPhone(phone.trim());
        address.setAddressLine1(addressLine1.trim());
        address.setAddressLine2(addressLine2 != null ? addressLine2.trim() : "");
        address.setCity(city.trim());
        address.setState(state.trim());
        address.setPostalCode(postalCode.trim());
        address.setCountry("India");
        address.setDefault(isDefault);

        addressDAO.create(address);
        session.setAttribute("flashSuccess", "Address added successfully!");
    }

    private void handleDeleteAddress(HttpServletRequest req, HttpSession session, User user) {
        String addressIdStr = req.getParameter("addressId");
        if (addressIdStr != null) {
            try {
                int addressId = Integer.parseInt(addressIdStr);
                addressDAO.delete(addressId, user.getUserId());
                session.setAttribute("flashSuccess", "Address removed.");
            } catch (NumberFormatException ignored) {
            }
        }
    }
}
