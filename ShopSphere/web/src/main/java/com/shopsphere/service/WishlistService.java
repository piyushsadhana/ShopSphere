package com.shopsphere.service;

import com.shopsphere.dao.WishlistDAO;
import com.shopsphere.model.Wishlist;

import java.util.List;

/**
 * Service managing customer Wishlist operations.
 */
public class WishlistService {

    private final WishlistDAO wishlistDAO = new WishlistDAO();

    public List<Wishlist> getWishlist(int userId) {
        return wishlistDAO.findByUserId(userId);
    }

    public boolean addToWishlist(int userId, int productId) {
        return wishlistDAO.addItem(userId, productId);
    }

    public boolean removeFromWishlist(int userId, int productId) {
        return wishlistDAO.removeItem(userId, productId);
    }

    public boolean isInWishlist(int userId, int productId) {
        return wishlistDAO.isInWishlist(userId, productId);
    }

    public int getWishlistCount(int userId) {
        return wishlistDAO.countItems(userId);
    }
}
