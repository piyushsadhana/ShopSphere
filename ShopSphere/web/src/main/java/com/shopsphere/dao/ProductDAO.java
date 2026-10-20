package com.shopsphere.dao;

import com.shopsphere.model.Product;
import com.shopsphere.util.DBConnection;

import java.math.BigDecimal;
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
 * Data Access Object for Product catalog management.
 */
public class ProductDAO {
    private static final Logger LOGGER = Logger.getLogger(ProductDAO.class.getName());

    public Optional<Product> findById(int productId) {
        String sql = "SELECT p.*, c.name AS category_name, " +
                     "COALESCE(AVG(r.rating), 0) AS avg_rating, " +
                     "COUNT(r.review_id) AS rev_count " +
                     "FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.category_id " +
                     "LEFT JOIN reviews r ON p.product_id = r.product_id " +
                     "WHERE p.product_id = ? " +
                     "GROUP BY p.product_id, c.name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapProductWithReviews(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding product by ID: " + productId, e);
        }
        return Optional.empty();
    }

    public List<Product> findFeatured(int limit) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name AS category_name, " +
                     "COALESCE(AVG(r.rating), 0) AS avg_rating, " +
                     "COUNT(r.review_id) AS rev_count " +
                     "FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.category_id " +
                     "LEFT JOIN reviews r ON p.product_id = r.product_id " +
                     "WHERE p.status = 'ACTIVE' " +
                     "GROUP BY p.product_id, c.name " +
                     "ORDER BY p.discount DESC, p.created_at DESC " +
                     "LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit > 0 ? limit : 8);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapProductWithReviews(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding featured products", e);
        }
        return list;
    }

    public List<Product> searchAndFilter(boolean activeOnly, Integer categoryId, String query,
                                         BigDecimal minPrice, BigDecimal maxPrice, String sortBy) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.*, c.name AS category_name, ")
           .append("COALESCE(AVG(r.rating), 0) AS avg_rating, ")
           .append("COUNT(r.review_id) AS rev_count ")
           .append("FROM products p ")
           .append("LEFT JOIN categories c ON p.category_id = c.category_id ")
           .append("LEFT JOIN reviews r ON p.product_id = r.product_id ")
           .append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (activeOnly) {
            sql.append("AND p.status = 'ACTIVE' ");
        }
        if (categoryId != null && categoryId > 0) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }
        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (LOWER(p.name) LIKE ? OR LOWER(p.brand) LIKE ? OR LOWER(p.description) LIKE ?) ");
            String wildcard = "%" + query.trim().toLowerCase() + "%";
            params.add(wildcard);
            params.add(wildcard);
            params.add(wildcard);
        }
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) >= 0) {
            sql.append("AND p.price >= ? ");
            params.add(minPrice);
        }
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
            sql.append("AND p.price <= ? ");
            params.add(maxPrice);
        }

        sql.append("GROUP BY p.product_id, c.name ");

        // Sorting
        if ("price_asc".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY p.price ASC ");
        } else if ("price_desc".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY p.price DESC ");
        } else if ("name_asc".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY p.name ASC ");
        } else if ("name_desc".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY p.name DESC ");
        } else if ("discount".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY p.discount DESC ");
        } else {
            sql.append("ORDER BY p.created_at DESC ");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapProductWithReviews(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching and filtering products", e);
        }
        return list;
    }

    public Product create(Product product) throws SQLException {
        String sql = "INSERT INTO products (category_id, name, brand, description, price, discount, stock, image_url, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, product.getCategoryId());
            ps.setString(2, product.getName());
            ps.setString(3, product.getBrand());
            ps.setString(4, product.getDescription());
            ps.setBigDecimal(5, product.getPrice());
            ps.setBigDecimal(6, product.getDiscount());
            ps.setInt(7, product.getStock());
            ps.setString(8, product.getImageUrl());
            ps.setString(9, product.getStatus() != null ? product.getStatus().name() : Product.Status.ACTIVE.name());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        product.setProductId(keys.getInt(1));
                    }
                }
            }
            return product;
        }
    }

    public boolean update(Product product) {
        String sql = "UPDATE products SET category_id = ?, name = ?, brand = ?, description = ?, " +
                     "price = ?, discount = ?, stock = ?, image_url = ?, status = ? WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, product.getCategoryId());
            ps.setString(2, product.getName());
            ps.setString(3, product.getBrand());
            ps.setString(4, product.getDescription());
            ps.setBigDecimal(5, product.getPrice());
            ps.setBigDecimal(6, product.getDiscount());
            ps.setInt(7, product.getStock());
            ps.setString(8, product.getImageUrl());
            ps.setString(9, product.getStatus() != null ? product.getStatus().name() : Product.Status.ACTIVE.name());
            ps.setInt(10, product.getProductId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating product ID: " + product.getProductId(), e);
            return false;
        }
    }

    public boolean delete(int productId) {
        // Safe delete: check if referenced in orders
        String sqlCheck = "SELECT COUNT(*) FROM order_items WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
            psCheck.setInt(1, productId);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Soft-archive to preserve past orders
                    String sqlArchive = "UPDATE products SET status = 'ARCHIVED' WHERE product_id = ?";
                    try (PreparedStatement psArch = conn.prepareStatement(sqlArchive)) {
                        psArch.setInt(1, productId);
                        return psArch.executeUpdate() > 0;
                    }
                }
            }

            String sqlDelete = "DELETE FROM products WHERE product_id = ?";
            try (PreparedStatement psDel = conn.prepareStatement(sqlDelete)) {
                psDel.setInt(1, productId);
                return psDel.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting/archiving product ID: " + productId, e);
            return false;
        }
    }

    /**
     * Decreases stock within an ongoing database transaction.
     */
    public boolean reduceStock(Connection conn, int productId, int quantity) throws SQLException {
        String sql = "UPDATE products SET stock = stock - ? WHERE product_id = ? AND stock >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            return ps.executeUpdate() > 0;
        }
    }

    public int countProducts() {
        String sql = "SELECT COUNT(*) FROM products WHERE status = 'ACTIVE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting products", e);
        }
        return 0;
    }

    private Product mapProductWithReviews(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setCategoryName(rs.getString("category_name"));
        p.setName(rs.getString("name"));
        p.setBrand(rs.getString("brand"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setDiscount(rs.getBigDecimal("discount"));
        p.setStock(rs.getInt("stock"));
        p.setImageUrl(rs.getString("image_url"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            try {
                p.setStatus(Product.Status.valueOf(statusStr));
            } catch (IllegalArgumentException ignored) {
                p.setStatus(Product.Status.ACTIVE);
            }
        }
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));

        p.setAverageRating(rs.getDouble("avg_rating"));
        p.setReviewCount(rs.getInt("rev_count"));
        return p;
    }
}
