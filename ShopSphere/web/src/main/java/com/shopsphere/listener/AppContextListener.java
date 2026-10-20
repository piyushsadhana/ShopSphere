package com.shopsphere.listener;

import com.shopsphere.dao.CategoryDAO;
import com.shopsphere.model.Category;
import com.shopsphere.util.DBConnection;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Initializes application-wide resources on startup and cleans up on shutdown.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        LOGGER.info("ShopSphere E-Commerce Application Initializing...");

        // Verify database connectivity
        boolean dbOk = DBConnection.testConnection();
        if (dbOk) {
            LOGGER.info("Database connection successfully established!");
            try {
                // Preload active categories for application navbar
                CategoryDAO categoryDAO = new CategoryDAO();
                List<Category> categories = categoryDAO.findAll(true);
                context.setAttribute("globalCategories", categories);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to preload categories: " + e.getMessage());
            }
        } else {
            LOGGER.warning("Database connectivity test failed on startup. Please verify db.properties or environment.");
        }

        context.setAttribute("appName", "ShopSphere");
        LOGGER.info("ShopSphere E-Commerce Application Started Successfully.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("ShopSphere Application Shutting Down...");

        // Unregister JDBC drivers to prevent Tomcat classloader leaks
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getClassLoader() == cl) {
                try {
                    DriverManager.deregisterDriver(driver);
                    LOGGER.info("Deregistered JDBC driver: " + driver);
                } catch (SQLException ex) {
                    LOGGER.log(Level.WARNING, "Error deregistering JDBC driver: " + driver, ex);
                }
            }
        }
    }
}
