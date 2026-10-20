package com.shopsphere.dao;

import com.shopsphere.model.Category;
import com.shopsphere.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Category entity.
 */
public class CategoryDAO {
    private static final Logger LOGGER = Logger.getLogger(CategoryDAO.class.getName());

    public List<Category> findAll(boolean activeOnly) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT c.*, (SELECT COUNT(*) FROM products p WHERE p.category_id = c.category_id AND p.status = 'ACTIVE') AS prod_count " +
                     "FROM categories c " +
                     (activeOnly ? "WHERE c.status = 'ACTIVE' " : "") +
                     "ORDER BY c.name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Category c = mapCategory(rs);
                c.setProductCount(rs.getInt("prod_count"));
                list.add(c);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding categories", e);
        }
        return list;
    }

    public Optional<Category> findById(int categoryId) {
        String sql = "SELECT c.*, (SELECT COUNT(*) FROM products p WHERE p.category_id = c.category_id) AS prod_count " +
                     "FROM categories c WHERE c.category_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Category c = mapCategory(rs);
                    c.setProductCount(rs.getInt("prod_count"));
                    return Optional.of(c);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding category by ID: " + categoryId, e);
        }
        return Optional.empty();
    }

    public Category create(Category category) throws SQLException {
        String sql = "INSERT INTO categories (name, description, image_url, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setString(3, category.getImageUrl());
            ps.setString(4, category.getStatus() != null ? category.getStatus().name() : Category.Status.ACTIVE.name());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        category.setCategoryId(keys.getInt(1));
                    }
                }
            }
            return category;
        }
    }

    public boolean update(Category category) {
        String sql = "UPDATE categories SET name = ?, description = ?, image_url = ?, status = ? WHERE category_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setString(3, category.getImageUrl());
            ps.setString(4, category.getStatus() != null ? category.getStatus().name() : Category.Status.ACTIVE.name());
            ps.setInt(5, category.getCategoryId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating category ID: " + category.getCategoryId(), e);
            return false;
        }
    }

    public boolean delete(int categoryId) {
        // Safe delete: check if products exist first
        String sqlCheck = "SELECT COUNT(*) FROM products WHERE category_id = ?";
        String sqlDelete = "DELETE FROM categories WHERE category_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
            psCheck.setInt(1, categoryId);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Has products: softly mark INACTIVE instead of throwing constraint violation
                    String sqlSoft = "UPDATE categories SET status = 'INACTIVE' WHERE category_id = ?";
                    try (PreparedStatement psSoft = conn.prepareStatement(sqlSoft)) {
                        psSoft.setInt(1, categoryId);
                        return psSoft.executeUpdate() > 0;
                    }
                }
            }

            try (PreparedStatement psDel = conn.prepareStatement(sqlDelete)) {
                psDel.setInt(1, categoryId);
                return psDel.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting category ID: " + categoryId, e);
            return false;
        }
    }

    public int countCategories() {
        String sql = "SELECT COUNT(*) FROM categories WHERE status = 'ACTIVE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting categories", e);
        }
        return 0;
    }

    private Category mapCategory(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setCategoryId(rs.getInt("category_id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        c.setImageUrl(rs.getString("image_url"));
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            try {
                c.setStatus(Category.Status.valueOf(statusStr));
            } catch (IllegalArgumentException ignored) {
                c.setStatus(Category.Status.ACTIVE);
            }
        }
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }
}
