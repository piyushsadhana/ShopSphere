package com.shopsphere.controller;

import com.shopsphere.model.User;
import com.shopsphere.service.ReviewService;
import com.shopsphere.util.CsrfUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller for submitting customer product reviews and ratings.
 */
@WebServlet(name = "ReviewServlet", urlPatterns = {"/add-review"})
public class ReviewServlet extends HttpServlet {

    private final ReviewService reviewService = new ReviewService();

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
            resp.sendRedirect(req.getContextPath() + "/products");
            return;
        }

        String prodIdStr = req.getParameter("productId");
        String ratingStr = req.getParameter("rating");
        String reviewText = req.getParameter("reviewText");

        if (prodIdStr != null && ratingStr != null) {
            try {
                int productId = Integer.parseInt(prodIdStr.trim());
                int rating = Integer.parseInt(ratingStr.trim());
                reviewService.submitReview(user.getUserId(), productId, rating, reviewText);
                session.setAttribute("flashSuccess", "Thank you! Your review has been submitted.");
                resp.sendRedirect(req.getContextPath() + "/product-details?id=" + productId);
                return;
            } catch (Exception e) {
                session.setAttribute("flashError", e.getMessage());
            }
        }

        resp.sendRedirect(req.getContextPath() + "/products");
    }
}
