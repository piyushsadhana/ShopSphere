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

/**
 * Controller for Customer Registration.
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }
        CsrfUtil.getOrCreateToken(req.getSession(true));
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!CsrfUtil.isValidToken(req)) {
            req.setAttribute("errorMessage", "Invalid security token. Please try again.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        // Retain input fields on error
        req.setAttribute("paramName", name);
        req.setAttribute("paramEmail", email);
        req.setAttribute("paramPhone", phone);

        if (password == null || !password.equals(confirmPassword)) {
            req.setAttribute("errorMessage", "Passwords do not match.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        try {
            User newUser = userService.register(name, email, password, phone);
            // Auto login or redirect to login page with success notification
            resp.sendRedirect(req.getContextPath() + "/login.jsp?success=registered");
        } catch (IllegalArgumentException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "An unexpected error occurred during registration. Please try again.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }
    }
}
