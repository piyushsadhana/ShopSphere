package com.shopsphere.dao;

import com.shopsphere.model.Coupon;
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
 * Data Access Object for Coupons and Discounts.
 */
public class CouponDAO {
    private static final Logger LOGGER = Logger.getLogger(CouponDAO.class.getName());

    public Optional<Coupon> findByCode(String code) {
        if (code == null) {
            return Optional.empty();
        }
        String sql = "SELECT * FROM coupons WHERE UPPER(code) = UPPER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapCoupon(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding coupon by code: " + code, e);
        }
        return Optional.empty();
    }

    public Optional<Coupon> findById(int couponId) {
        String sql = "SELECT * FROM coupons WHERE coupon_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, couponId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapCoupon(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding coupon by ID: " + couponId, e);
        }
        return Optional.empty();
    }

    public List<Coupon> findAll() {
        List<Coupon> list = new ArrayList<>();
        String sql = "SELECT * FROM coupons ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapCoupon(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all coupons", e);
        }
        return list;
    }

    public Coupon create(Coupon coupon) throws SQLException {
        String sql = "INSERT INTO coupons (code, discount_type, discount_value, min_order_value, usage_limit, used_count, expiry_date, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, coupon.getCode().trim().toUpperCase());
            ps.setString(2, coupon.getDiscountType() != null ? coupon.getDiscountType().name() : Coupon.DiscountType.PERCENTAGE.name());
            ps.setBigDecimal(3, coupon.getDiscountValue());
            ps.setBigDecimal(4, coupon.getMinOrderValue());
            ps.setInt(5, coupon.getUsageLimit());
            ps.setInt(6, coupon.getUsedCount());
            ps.setDate(7, coupon.getExpiryDate());
            ps.setString(8, coupon.getStatus() != null ? coupon.getStatus().name() : Coupon.Status.ACTIVE.name());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        coupon.setCouponId(keys.getInt(1));
                    }
                }
            }
            return coupon;
        }
    }

    public boolean update(Coupon coupon) {
        String sql = "UPDATE coupons SET code = ?, discount_type = ?, discount_value = ?, min_order_value = ?, " +
                     "usage_limit = ?, used_count = ?, expiry_date = ?, status = ? WHERE coupon_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, coupon.getCode().trim().toUpperCase());
            ps.setString(2, coupon.getDiscountType() != null ? coupon.getDiscountType().name() : Coupon.DiscountType.PERCENTAGE.name());
            ps.setBigDecimal(3, coupon.getDiscountValue());
            ps.setBigDecimal(4, coupon.getMinOrderValue());
            ps.setInt(5, coupon.getUsageLimit());
            ps.setInt(6, coupon.getUsedCount());
            ps.setDate(7, coupon.getExpiryDate());
            ps.setString(8, coupon.getStatus() != null ? coupon.getStatus().name() : Coupon.Status.ACTIVE.name());
            ps.setInt(9, coupon.getCouponId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating coupon ID: " + coupon.getCouponId(), e);
            return false;
        }
    }

    public boolean delete(int couponId) {
        String sql = "DELETE FROM coupons WHERE coupon_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, couponId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting coupon ID: " + couponId, e);
            return false;
        }
    }

    public boolean incrementUsage(int couponId, Connection conn) throws SQLException {
        String sql = "UPDATE coupons SET used_count = used_count + 1 WHERE coupon_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, couponId);
            return ps.executeUpdate() > 0;
        }
    }

    private Coupon mapCoupon(ResultSet rs) throws SQLException {
        Coupon c = new Coupon();
        c.setCouponId(rs.getInt("coupon_id"));
        c.setCode(rs.getString("code"));

        String dt = rs.getString("discount_type");
        if (dt != null) {
            try {
                c.setDiscountType(Coupon.DiscountType.valueOf(dt));
            } catch (IllegalArgumentException ignored) {
                c.setDiscountType(Coupon.DiscountType.PERCENTAGE);
            }
        }

        c.setDiscountValue(rs.getBigDecimal("discount_value"));
        c.setMinOrderValue(rs.getBigDecimal("min_order_value"));
        c.setUsageLimit(rs.getInt("usage_limit"));
        c.setUsedCount(rs.getInt("used_count"));
        c.setExpiryDate(rs.getDate("expiry_date"));

        String st = rs.getString("status");
        if (st != null) {
            try {
                c.setStatus(Coupon.Status.valueOf(st));
            } catch (IllegalArgumentException ignored) {
                c.setStatus(Coupon.Status.ACTIVE);
            }
        }

        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }
}
