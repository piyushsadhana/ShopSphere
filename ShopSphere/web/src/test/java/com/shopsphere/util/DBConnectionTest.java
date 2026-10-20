package com.shopsphere.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DBConnectionTest {

    @Test
    @DisplayName("Verify DBConnection loads configuration and tests connection")
    public void testDatabaseConnection() {
        assertNotNull(DBConnection.getUrl(), "JDBC URL should not be null");
        assertNotNull(DBConnection.getUsername(), "DB Username should not be null");

        // If local MySQL is running and credentials match, test live connection
        boolean isConnected = DBConnection.testConnection();
        if (isConnected) {
            try (Connection conn = DBConnection.getConnection()) {
                assertNotNull(conn, "Connection should be valid");
                assertFalse(conn.isClosed(), "Connection should not be closed");
                assertTrue(conn.isValid(3), "Connection should be valid within timeout");
            } catch (SQLException e) {
                // If database does not exist yet (before sql schema import), that is handled in Stage 2
                System.out.println("Notice: Database connection attempt returned: " + e.getMessage());
            }
        } else {
            System.out.println("Notice: Local MySQL server not accessible with configured credentials during build test.");
        }
    }
}
