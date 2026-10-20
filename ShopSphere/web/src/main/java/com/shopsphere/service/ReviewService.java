package com.shopsphere.service;

import com.shopsphere.dao.ReviewDAO;
import com.shopsphere.model.Review;
import com.shopsphere.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * Service managing customer ratings and reviews.
 */
public class ReviewService {

    private final ReviewDAO reviewDAO = new ReviewDAO();

    public List<Review> getReviewsForProduct(int productId) {
        return reviewDAO.findByProductId(productId);
    }

    public boolean hasUserReviewed(int userId, int productId) {
        return reviewDAO.hasUserReviewed(userId, productId);
    }

    public Review submitReview(int userId, int productId, int rating, String reviewText) throws SQLException {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5 stars.");
        }
        Review r = new Review();
        r.setUserId(userId);
        r.setProductId(productId);
        r.setRating(rating);
        r.setReviewText(reviewText != null ? ValidationUtil.sanitize(reviewText.trim()) : "");

        return reviewDAO.create(r);
    }

    public List<Review> getAllReviews() {
        return reviewDAO.findAll();
    }

    public boolean deleteReview(int reviewId) {
        return reviewDAO.delete(reviewId);
    }
}
