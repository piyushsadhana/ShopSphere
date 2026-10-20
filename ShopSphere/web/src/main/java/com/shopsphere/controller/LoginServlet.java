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
import java.util.Optional;

/**
 * Controller for User Authentication (Login) with Session Fixation Protection.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            User user = (User) session.getAttribute("currentUser");
            if (user.isAdmin()) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            } else {
                resp.sendRedirect(req.getContextPath() + "/index.jsp");
            }
            return;
        }

        CsrfUtil.getOrCreateToken(req.getSession(true));

        // Check query parameters for feedback messages
        String errorParam = req.getParameter("error");
        if ("auth_required".equalsIgnoreCase(errorParam)) {
            req.setAttribute("errorMessage", "Please log in to continue.");
        } else if ("admin_required".equalsIgnoreCase(errorParam)) {
            req.setAttribute("errorMessage", "Administrator privileges required.");
        }

        String successParam = req.getParameter("success");
        if ("registered".equalsIgnoreCase(successParam)) {
            req.setAttribute("successMessage", "Registration successful! You can now log in.");
        } else if ("logged_out".equalsIgnoreCase(successParam)) {
            req.setAttribute("successMessage", "You have been logged out successfully.");
        }

        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!CsrfUtil.isValidToken(req)) {
            req.setAttribute("errorMessage", "Invalid security token. Please try again.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        String email = req.getParameter("email");
        String password = req.getParameter("password");
        req.setAttribute("paramEmail", email);

        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            req.setAttribute("errorMessage", "Please enter both email and password.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        Optional<User> userOpt = userService.authenticate(email, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Retrieve returnUrl before session invalidation
            HttpSession oldSession = req.getSession(false);
            String returnUrl = null;
            if (oldSession != null) {
                returnUrl = (String) oldSession.getAttribute("returnUrl");
                oldSession.invalidate(); // Session Fixation Prevention
            }

            // Create fresh session
            HttpSession newSession = req.getSession(true);
            newSession.setAttribute("currentUser", user);
            CsrfUtil.getOrCreateToken(newSession);

            if (returnUrl != null && !returnUrl.isEmpty()) {
                resp.sendRedirect(returnUrl);
            } else if (user.isAdmin()) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            } else {
                resp.sendRedirect(req.getContextPath() + "/index.jsp");
            }
        } else {
            req.setAttribute("errorMessage", "Invalid email or password.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
