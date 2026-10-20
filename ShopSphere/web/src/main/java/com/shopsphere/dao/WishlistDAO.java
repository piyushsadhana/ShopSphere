package com.shopsphere.dao;

import com.shopsphere.model.Product;
import com.shopsphere.model.Wishlist;
import com.shopsphere.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Customer Wishlist.
 */
public class WishlistDAO {
    private static final Logger LOGGER = Logger.getLogger(WishlistDAO.class.getName());

    public List<Wishlist> findByUserId(int userId) {
        List<Wishlist> list = new ArrayList<>();
        String sql = "SELECT w.*, p.category_id, p.name AS prod_name, p.brand, p.description, " +
                     "p.price, p.discount, p.stock, p.image_url, p.status AS prod_status, " +
                     "c.name AS category_name " +
                     "FROM wishlist w " +
                     "JOIN products p ON w.product_id = p.product_id " +
                     "LEFT JOIN categories c ON p.category_id = c.category_id " +
                     "WHERE w.user_id = ? " +
                     "ORDER BY w.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Wishlist w = new Wishlist();
                    w.setWishlistId(rs.getInt("wishlist_id"));
                    w.setUserId(rs.getInt("user_id"));
                    w.setProductId(rs.getInt("product_id"));
                    w.setCreatedAt(rs.getTimestamp("created_at"));

                    Product p = new Product();
                    p.setProductId(rs.getInt("product_id"));
                    p.setCategoryId(rs.getInt("category_id"));
                    p.setCategoryName(rs.getString("category_name"));
                    p.setName(rs.getString("prod_name"));
                    p.setBrand(rs.getString("brand"));
                    p.setDescription(rs.getString("description"));
                    p.setPrice(rs.getBigDecimal("price"));
                    p.setDiscount(rs.getBigDecimal("discount"));
                    p.setStock(rs.getInt("stock"));
                    p.setImageUrl(rs.getString("image_url"));
                    String statusStr = rs.getString("prod_status");
                    if (statusStr != null) {
                        try {
                            p.setStatus(Product.Status.valueOf(statusStr));
                        } catch (IllegalArgumentException ignored) {
                            p.setStatus(Product.Status.ACTIVE);
                        }
                    }

                    w.setProduct(p);
                    list.add(w);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding wishlist for user ID: " + userId, e);
        }
        return list;
    }

    public boolean addItem(int userId, int productId) {
        String sql = "INSERT IGNORE INTO wishlist (user_id, product_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding to wishlist for user ID: " + userId, e);
            return false;
        }
    }

    public boolean removeItem(int userId, int productId) {
        String sql = "DELETE FROM wishlist WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing from wishlist for user ID: " + userId, e);
            return false;
        }
    }

    public boolean isInWishlist(int userId, int productId) {
        String sql = "SELECT 1 FROM wishlist WHERE user_id = ? AND product_id = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking wishlist item existence", e);
            return false;
        }
    }

    public int countItems(int userId) {
        String sql = "SELECT COUNT(*) FROM wishlist WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting wishlist items for user ID: " + userId, e);
        }
        return 0;
    }
}
