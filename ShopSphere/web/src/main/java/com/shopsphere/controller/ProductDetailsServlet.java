package com.shopsphere.controller;

import com.shopsphere.model.Product;
import com.shopsphere.model.Review;
import com.shopsphere.model.User;
import com.shopsphere.service.ProductService;
import com.shopsphere.service.ReviewService;
import com.shopsphere.service.WishlistService;
import com.shopsphere.util.CsrfUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controller displaying full details, specifications, and customer reviews for a product.
 */
@WebServlet(name = "ProductDetailsServlet", urlPatterns = {"/product-details"})
public class ProductDetailsServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final ReviewService reviewService = new ReviewService();
    private final WishlistService wishlistService = new WishlistService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/products");
            return;
        }

        try {
            int productId = Integer.parseInt(idParam.trim());
            Optional<Product> prodOpt = productService.getProductById(productId);

            if (prodOpt.isEmpty() || !prodOpt.get().isActive()) {
                resp.sendRedirect(req.getContextPath() + "/products");
                return;
            }

            Product product = prodOpt.get();
            List<Review> reviews = reviewService.getReviewsForProduct(productId);
            List<Product> relatedProducts = productService.getActiveProducts(product.getCategoryId(), null, null, null, null);

            // Filter out current product from related items
            relatedProducts.removeIf(p -> p.getProductId() == productId);
            if (relatedProducts.size() > 4) {
                relatedProducts = relatedProducts.subList(0, 4);
            }

            HttpSession session = req.getSession(false);
            User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
            boolean alreadyReviewed = false;
            boolean inWishlist = false;

            if (user != null) {
                alreadyReviewed = reviewService.hasUserReviewed(user.getUserId(), productId);
                inWishlist = wishlistService.isInWishlist(user.getUserId(), productId);
            }

            CsrfUtil.getOrCreateToken(req.getSession(true));

            req.setAttribute("product", product);
            req.setAttribute("reviews", reviews);
            req.setAttribute("relatedProducts", relatedProducts);
            req.setAttribute("alreadyReviewed", alreadyReviewed);
            req.setAttribute("inWishlist", inWishlist);

            req.getRequestDispatcher("/product-details.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/products");
        }
    }
}
