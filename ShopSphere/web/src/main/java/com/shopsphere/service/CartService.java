package com.shopsphere.service;

import com.shopsphere.dao.CartDAO;
import com.shopsphere.dao.ProductDAO;
import com.shopsphere.model.CartItem;
import com.shopsphere.model.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service managing shopping cart business logic.
 * Supports persistent database cart for logged-in customers
 * and session-based cart for guest visitors.
 */
public class CartService {

    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public CartService() {
        this.cartDAO = new CartDAO();
        this.productDAO = new ProductDAO();
    }

    public CartService(CartDAO cartDAO, ProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    /**
     * Gets user cart items, recalculating current server-side product prices and availability.
     */
    public List<CartItem> getCartForUser(int userId) {
        return cartDAO.findByUserId(userId);
    }

    /**
     * Adds an item to the user's cart after validating stock.
     */
    public boolean addToCart(int userId, int productId, int quantity) throws IllegalArgumentException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        Optional<Product> prodOpt = productDAO.findById(productId);
        if (prodOpt.isEmpty() || !prodOpt.get().isActive()) {
            throw new IllegalArgumentException("Product is unavailable.");
        }
        Product product = prodOpt.get();
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Only " + product.getStock() + " units available in stock.");
        }
        return cartDAO.addItem(userId, productId, quantity);
    }

    public boolean updateQuantity(int userId, int productId, int quantity) throws IllegalArgumentException {
        if (quantity <= 0) {
            return cartDAO.removeItem(userId, productId);
        }
        Optional<Product> prodOpt = productDAO.findById(productId);
        if (prodOpt.isEmpty() || !prodOpt.get().isActive()) {
            return cartDAO.removeItem(userId, productId);
        }
        Product product = prodOpt.get();
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Only " + product.getStock() + " units available in stock.");
        }
        return cartDAO.updateQuantity(userId, productId, quantity);
    }

    public boolean removeFromCart(int userId, int productId) {
        return cartDAO.removeItem(userId, productId);
    }

    public int getCartCount(int userId) {
        return cartDAO.countItems(userId);
    }

    /**
     * Calculates order subtotal from cart items based on authoritative server pricing.
     */
    public BigDecimal calculateSubtotal(List<CartItem> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            if (item.getProduct() != null) {
                total = total.add(item.getSubtotal());
            }
        }
        return total;
    }
}
