package com.shopsphere.dao;

import com.shopsphere.model.Order;
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
 * Data Access Object for Order transactions and management.
 */
public class OrderDAO {
    private static final Logger LOGGER = Logger.getLogger(OrderDAO.class.getName());

    public Order create(Order order, Connection conn) throws SQLException {
        String sql = "INSERT INTO orders (order_number, user_id, address_snapshot, total_amount, " +
                     "discount_amount, shipping_fee, tax_amount, final_amount, order_status, " +
                     "payment_status, payment_method, coupon_code, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, order.getOrderNumber());
            ps.setInt(2, order.getUserId());
            ps.setString(3, order.getAddressSnapshot());
            ps.setBigDecimal(4, order.getTotalAmount());
            ps.setBigDecimal(5, order.getDiscountAmount());
            ps.setBigDecimal(6, order.getShippingFee());
            ps.setBigDecimal(7, order.getTaxAmount());
            ps.setBigDecimal(8, order.getFinalAmount());
            ps.setString(9, order.getOrderStatus() != null ? order.getOrderStatus().name() : Order.OrderStatus.PENDING.name());
            ps.setString(10, order.getPaymentStatus() != null ? order.getPaymentStatus().name() : Order.PaymentStatus.PENDING.name());
            ps.setString(11, order.getPaymentMethod());
            ps.setString(12, order.getCouponCode());
            ps.setString(13, order.getNotes());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        order.setOrderId(keys.getInt(1));
                    }
                }
            }
            return order;
        }
    }

    public Optional<Order> findById(int orderId) {
        String sql = "SELECT o.*, u.name AS cust_name, u.email AS cust_email " +
                     "FROM orders o " +
                     "LEFT JOIN users u ON o.user_id = u.user_id " +
                     "WHERE o.order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapOrder(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding order ID: " + orderId, e);
        }
        return Optional.empty();
    }

    public Optional<Order> findByOrderNumber(String orderNumber) {
        String sql = "SELECT o.*, u.name AS cust_name, u.email AS cust_email " +
                     "FROM orders o " +
                     "LEFT JOIN users u ON o.user_id = u.user_id " +
                     "WHERE o.order_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapOrder(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding order by number: " + orderNumber, e);
        }
        return Optional.empty();
    }

    public List<Order> findByUserId(int userId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, u.name AS cust_name, u.email AS cust_email " +
                     "FROM orders o " +
                     "LEFT JOIN users u ON o.user_id = u.user_id " +
                     "WHERE o.user_id = ? " +
                     "ORDER BY o.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapOrder(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding orders for user ID: " + userId, e);
        }
        return list;
    }

    public List<Order> findAll(String statusFilter) {
        List<Order> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT o.*, u.name AS cust_name, u.email AS cust_email ")
           .append("FROM orders o ")
           .append("LEFT JOIN users u ON o.user_id = u.user_id ");

        boolean hasFilter = statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter);
        if (hasFilter) {
            sql.append("WHERE o.order_status = ? ");
        }
        sql.append("ORDER BY o.created_at DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (hasFilter) {
                ps.setString(1, statusFilter.trim());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapOrder(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all orders", e);
        }
        return list;
    }

    public boolean updateStatus(int orderId, Order.OrderStatus orderStatus, Order.PaymentStatus paymentStatus) {
        StringBuilder sql = new StringBuilder("UPDATE orders SET ");
        List<Object> params = new ArrayList<>();

        if (orderStatus != null) {
            sql.append("order_status = ?");
            params.add(orderStatus.name());
        }
        if (paymentStatus != null) {
            if (!params.isEmpty()) {
                sql.append(", ");
            }
            sql.append("payment_status = ?");
            params.add(paymentStatus.name());
        }
        sql.append(" WHERE order_id = ?");
        params.add(orderId);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating status for order ID: " + orderId, e);
            return false;
        }
    }

    public int countOrders() {
        String sql = "SELECT COUNT(*) FROM orders";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting orders", e);
        }
        return 0;
    }

    public BigDecimal getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(final_amount), 0) FROM orders WHERE payment_status = 'PAID'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calculating total revenue", e);
        }
        return BigDecimal.ZERO;
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setOrderId(rs.getInt("order_id"));
        o.setOrderNumber(rs.getString("order_number"));
        o.setUserId(rs.getInt("user_id"));
        o.setCustomerName(rs.getString("cust_name"));
        o.setCustomerEmail(rs.getString("cust_email"));
        o.setAddressSnapshot(rs.getString("address_snapshot"));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        o.setShippingFee(rs.getBigDecimal("shipping_fee"));
        o.setTaxAmount(rs.getBigDecimal("tax_amount"));
        o.setFinalAmount(rs.getBigDecimal("final_amount"));

        String os = rs.getString("order_status");
        if (os != null) {
            try {
                o.setOrderStatus(Order.OrderStatus.valueOf(os));
            } catch (IllegalArgumentException ignored) {
                o.setOrderStatus(Order.OrderStatus.PENDING);
            }
        }

        String ps = rs.getString("payment_status");
        if (ps != null) {
            try {
                o.setPaymentStatus(Order.PaymentStatus.valueOf(ps));
            } catch (IllegalArgumentException ignored) {
                o.setPaymentStatus(Order.PaymentStatus.PENDING);
            }
        }

        o.setPaymentMethod(rs.getString("payment_method"));
        o.setCouponCode(rs.getString("coupon_code"));
        o.setNotes(rs.getString("notes"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        o.setUpdatedAt(rs.getTimestamp("updated_at"));
        return o;
    }
}
