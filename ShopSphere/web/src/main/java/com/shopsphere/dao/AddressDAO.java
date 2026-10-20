package com.shopsphere.dao;

import com.shopsphere.model.Address;
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
 * Data Access Object for Address management.
 */
public class AddressDAO {
    private static final Logger LOGGER = Logger.getLogger(AddressDAO.class.getName());

    public List<Address> findByUserId(int userId) {
        List<Address> list = new ArrayList<>();
        String sql = "SELECT * FROM addresses WHERE user_id = ? ORDER BY is_default DESC, created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAddress(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding addresses for user ID: " + userId, e);
        }
        return list;
    }

    public Optional<Address> findById(int addressId) {
        String sql = "SELECT * FROM addresses WHERE address_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapAddress(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding address ID: " + addressId, e);
        }
        return Optional.empty();
    }

    public Address create(Address address) throws SQLException {
        // If set as default, reset previous defaults
        if (address.isDefault()) {
            resetDefault(address.getUserId());
        }

        String sql = "INSERT INTO addresses (user_id, recipient_name, phone, address_line1, address_line2, " +
                     "city, state, postal_code, country, is_default) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, address.getUserId());
            ps.setString(2, address.getRecipientName());
            ps.setString(3, address.getPhone());
            ps.setString(4, address.getAddressLine1());
            ps.setString(5, address.getAddressLine2());
            ps.setString(6, address.getCity());
            ps.setString(7, address.getState());
            ps.setString(8, address.getPostalCode());
            ps.setString(9, address.getCountry() != null ? address.getCountry() : "India");
            ps.setBoolean(10, address.isDefault());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        address.setAddressId(keys.getInt(1));
                    }
                }
            }
            return address;
        }
    }

    public boolean update(Address address) {
        if (address.isDefault()) {
            resetDefault(address.getUserId());
        }

        String sql = "UPDATE addresses SET recipient_name = ?, phone = ?, address_line1 = ?, address_line2 = ?, " +
                     "city = ?, state = ?, postal_code = ?, country = ?, is_default = ? " +
                     "WHERE address_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, address.getRecipientName());
            ps.setString(2, address.getPhone());
            ps.setString(3, address.getAddressLine1());
            ps.setString(4, address.getAddressLine2());
            ps.setString(5, address.getCity());
            ps.setString(6, address.getState());
            ps.setString(7, address.getPostalCode());
            ps.setString(8, address.getCountry());
            ps.setBoolean(9, address.isDefault());
            ps.setInt(10, address.getAddressId());
            ps.setInt(11, address.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating address ID: " + address.getAddressId(), e);
            return false;
        }
    }

    public boolean delete(int addressId, int userId) {
        String sql = "DELETE FROM addresses WHERE address_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting address ID: " + addressId, e);
            return false;
        }
    }

    private void resetDefault(int userId) {
        String sql = "UPDATE addresses SET is_default = FALSE WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error resetting default addresses for user ID: " + userId, e);
        }
    }

    private Address mapAddress(ResultSet rs) throws SQLException {
        Address a = new Address();
        a.setAddressId(rs.getInt("address_id"));
        a.setUserId(rs.getInt("user_id"));
        a.setRecipientName(rs.getString("recipient_name"));
        a.setPhone(rs.getString("phone"));
        a.setAddressLine1(rs.getString("address_line1"));
        a.setAddressLine2(rs.getString("address_line2"));
        a.setCity(rs.getString("city"));
        a.setState(rs.getString("state"));
        a.setPostalCode(rs.getString("postal_code"));
        a.setCountry(rs.getString("country"));
        a.setDefault(rs.getBoolean("is_default"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        return a;
    }
}
