package com.shopsphere.controller;

import com.google.gson.JsonObject;
import com.shopsphere.model.User;
import com.shopsphere.model.Wishlist;
import com.shopsphere.service.WishlistService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller for Customer Wishlist actions.
 */
@WebServlet(name = "WishlistServlet", urlPatterns = {"/wishlist", "/add-to-wishlist", "/remove-from-wishlist"})
public class WishlistServlet extends HttpServlet {

    private final WishlistService wishlistService = new WishlistService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?error=auth_required");
            return;
        }

        List<Wishlist> wishlist = wishlistService.getWishlist(user.getUserId());
        req.setAttribute("wishlist", wishlist);
        req.getRequestDispatcher("/wishlist.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"));

        if (user == null) {
            if (isAjax) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                JsonObject json = new JsonObject();
                json.addProperty("status", "unauthorized");
                json.addProperty("message", "Please log in to manage your wishlist.");
                resp.getWriter().write(json.toString());
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/login?error=auth_required");
            return;
        }

        String uri = req.getRequestURI();
        String prodIdStr = req.getParameter("productId");

        if (prodIdStr != null && !prodIdStr.trim().isEmpty()) {
            try {
                int productId = Integer.parseInt(prodIdStr.trim());
                if (uri.endsWith("/add-to-wishlist")) {
                    wishlistService.addToWishlist(user.getUserId(), productId);
                    if (isAjax) {
                        JsonObject json = new JsonObject();
                        json.addProperty("status", "success");
                        json.addProperty("message", "Added to wishlist!");
                        json.addProperty("count", wishlistService.getWishlistCount(user.getUserId()));
                        resp.getWriter().write(json.toString());
                        return;
                    }
                    session.setAttribute("flashSuccess", "Product added to wishlist!");
                } else if (uri.endsWith("/remove-from-wishlist")) {
                    wishlistService.removeFromWishlist(user.getUserId(), productId);
                    if (isAjax) {
                        JsonObject json = new JsonObject();
                        json.addProperty("status", "success");
                        json.addProperty("message", "Removed from wishlist!");
                        json.addProperty("count", wishlistService.getWishlistCount(user.getUserId()));
                        resp.getWriter().write(json.toString());
                        return;
                    }
                    session.setAttribute("flashSuccess", "Product removed from wishlist.");
                }
            } catch (NumberFormatException ignored) {
            }
        }

        String redirect = req.getParameter("redirect");
        if (redirect != null && !redirect.trim().isEmpty()) {
            resp.sendRedirect(redirect);
        } else {
            resp.sendRedirect(req.getContextPath() + "/wishlist");
        }
    }
}
