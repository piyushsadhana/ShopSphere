package com.shopsphere.controller;

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
 * Controller for Admin Customer and User Account Oversight.
 */
@WebServlet(name = "AdminUserServlet", urlPatterns = {"/admin/users", "/admin/update-user-status"})
public class AdminUserServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<User> users = userService.getAllUsers();
        req.setAttribute("users", users);

        CsrfUtil.getOrCreateToken(req.getSession(true));
        req.getRequestDispatcher("/admin/users.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        User currentUser = (User) session.getAttribute("currentUser");

        if (!CsrfUtil.isValidToken(req)) {
            session.setAttribute("flashError", "Invalid security token.");
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        }

        try {
            int targetUserId = Integer.parseInt(req.getParameter("userId"));
            String statusStr = req.getParameter("status");
            User.Status status = User.Status.valueOf(statusStr);

            // Prevent self-lockout
            if (currentUser != null && currentUser.getUserId() == targetUserId && !User.Status.ACTIVE.equals(status)) {
                session.setAttribute("flashError", "You cannot deactivate or suspend your own active admin account.");
                resp.sendRedirect(req.getContextPath() + "/admin/users");
                return;
            }

            boolean updated = userService.updateStatus(targetUserId, status);
            if (updated) {
                session.setAttribute("flashSuccess", "User account status updated to " + status + ".");
            } else {
                session.setAttribute("flashError", "Failed to update user status.");
            }
        } catch (Exception e) {
            session.setAttribute("flashError", e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }
}
