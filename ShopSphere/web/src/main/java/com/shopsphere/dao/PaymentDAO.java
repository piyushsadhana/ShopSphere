package com.shopsphere.dao;

import com.shopsphere.model.Payment;
import com.shopsphere.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Payment records.
 */
public class PaymentDAO {
    private static final Logger LOGGER = Logger.getLogger(PaymentDAO.class.getName());

    public Payment create(Payment payment, Connection conn) throws SQLException {
        String sql = "INSERT INTO payments (order_id, amount, method, status, transaction_ref) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, payment.getOrderId());
            ps.setBigDecimal(2, payment.getAmount());
            ps.setString(3, payment.getMethod());
            ps.setString(4, payment.getStatus() != null ? payment.getStatus().name() : Payment.Status.PENDING.name());
            ps.setString(5, payment.getTransactionRef());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        payment.setPaymentId(keys.getInt(1));
                    }
                }
            }
            return payment;
        }
    }

    public Optional<Payment> findByOrderId(int orderId) {
        String sql = "SELECT * FROM payments WHERE order_id = ? ORDER BY payment_date DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Payment p = new Payment();
                    p.setPaymentId(rs.getInt("payment_id"));
                    p.setOrderId(rs.getInt("order_id"));
                    p.setAmount(rs.getBigDecimal("amount"));
                    p.setMethod(rs.getString("method"));
                    String statusStr = rs.getString("status");
                    if (statusStr != null) {
                        try {
                            p.setStatus(Payment.Status.valueOf(statusStr));
                        } catch (IllegalArgumentException ignored) {
                            p.setStatus(Payment.Status.PENDING);
                        }
                    }
                    p.setTransactionRef(rs.getString("transaction_ref"));
                    p.setPaymentDate(rs.getTimestamp("payment_date"));
                    return Optional.of(p);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding payment for order ID: " + orderId, e);
        }
        return Optional.empty();
    }
}
