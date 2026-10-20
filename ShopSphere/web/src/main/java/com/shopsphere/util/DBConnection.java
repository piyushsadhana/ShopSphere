package com.shopsphere.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread-safe Database Connection utility for ShopSphere.
 * Manages JDBC connections using properties loaded from db.properties
 * with runtime overrides via System properties or Environment variables.
 */
public final class DBConnection {

    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    private static String url;
    private static String username;
    private static String password;
    private static String driverClassName;

    static {
        loadConfiguration();
    }

    private DBConnection() {
        // Utility class: prevent instantiation
    }

    /**
     * Loads database configuration from classpath properties file,
     * overridden by System properties or Environment variables.
     */
    public static synchronized void loadConfiguration() {
        Properties props = new Properties();

        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            } else {
                LOGGER.warning("db.properties not found on classpath, relying on defaults/environment.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error reading db.properties file: " + e.getMessage(), e);
        }

        // Driver Class
        driverClassName = getEnvOrProp("DB_DRIVER", "db.driver", props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));

        // JDBC URL
        String defaultUrl = "jdbc:mysql://localhost:3306/shopsphere?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
        url = getEnvOrProp("DB_URL", "db.url", props.getProperty("db.url", defaultUrl));

        // Credentials
        username = getEnvOrProp("DB_USER", "db.user", props.getProperty("db.user", "root"));
        password = getEnvOrProp("DB_PASSWORD", "db.password", props.getProperty("db.password", "456123"));

        try {
            Class.forName(driverClassName);
            LOGGER.info("MySQL JDBC Driver registered: " + driverClassName);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Could not load JDBC driver class: " + driverClassName, e);
        }
    }

    private static String getEnvOrProp(String envKey, String propKey, String fallback) {
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.trim().isEmpty()) {
            return envVal.trim();
        }
        String sysProp = System.getProperty(propKey);
        if (sysProp != null && !sysProp.trim().isEmpty()) {
            return sysProp.trim();
        }
        return fallback;
    }

    /**
     * Obtains a new JDBC connection to the ShopSphere database.
     *
     * @return active java.sql.Connection
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to obtain database connection to " + url + " as user " + username, ex);
            throw ex;
        }
    }

    /**
     * Utility method to test connectivity.
     *
     * @return true if connection succeeded, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed() && conn.isValid(5);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Database connectivity test failed: " + e.getMessage());
            return false;
        }
    }

    public static String getUrl() {
        return url;
    }

    public static String getUsername() {
        return username;
    }
}
