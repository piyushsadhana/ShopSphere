package com.shopsphere.dao;

import com.shopsphere.model.Category;
import com.shopsphere.model.Coupon;
import com.shopsphere.model.Product;
import com.shopsphere.model.User;
import com.shopsphere.util.DBConnection;
import com.shopsphere.util.PasswordUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DaoIntegrationTest {

    @BeforeAll
    public static void checkConnectivity() {
        boolean connected = DBConnection.testConnection();
        assertTrue(connected, "Database connection must be valid before running DAO tests");
    }

    @Test
    @DisplayName("Verify UserDAO queries and authentication lookup")
    public void testUserDAO() {
        UserDAO userDAO = new UserDAO();

        // 1. Look up admin user
        Optional<User> adminOpt = userDAO.findByEmail("admin@shopsphere.com");
        assertTrue(adminOpt.isPresent(), "Admin user should exist in database");
        User admin = adminOpt.get();
        assertEquals(User.Role.ADMIN, admin.getRole());
        assertTrue(admin.isAdmin());
        assertTrue(PasswordUtil.checkPassword("Admin@123", admin.getPassword()));

        // 2. Look up customer user
        Optional<User> customerOpt = userDAO.findByEmail("john@example.com");
        assertTrue(customerOpt.isPresent(), "Customer John Doe should exist in database");
        User customer = customerOpt.get();
        assertEquals(User.Role.CUSTOMER, customer.getRole());
        assertTrue(PasswordUtil.checkPassword("Customer@123", customer.getPassword()));

        // 3. Email exists check
        assertTrue(userDAO.existsByEmail("john@example.com"));
        assertFalse(userDAO.existsByEmail("nonexistent_user@example.com"));
    }

    @Test
    @DisplayName("Verify CategoryDAO queries")
    public void testCategoryDAO() {
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.findAll(true);
        assertNotNull(categories);
        assertFalse(categories.isEmpty(), "Categories should be populated from seed data");
        assertTrue(categories.size() >= 5, "At least 5 sample categories should exist");
    }

    @Test
    @DisplayName("Verify ProductDAO search, filter, and details")
    public void testProductDAO() {
        ProductDAO productDAO = new ProductDAO();

        // 1. Find by ID
        Optional<Product> prodOpt = productDAO.findById(1);
        assertTrue(prodOpt.isPresent(), "Product 1 should exist");
        Product p = prodOpt.get();
        assertNotNull(p.getName());
        assertTrue(p.getPrice().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(p.getFinalPrice().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(p.getFinalPrice().compareTo(p.getPrice()) <= 0);

        // 2. Featured products
        List<Product> featured = productDAO.findFeatured(8);
        assertNotNull(featured);
        assertFalse(featured.isEmpty(), "Featured products should not be empty");

        // 3. Search by keyword
        List<Product> searchResults = productDAO.searchAndFilter(true, null, "Headphones", null, null, null);
        assertFalse(searchResults.isEmpty(), "Searching 'Headphones' should return at least one product");

        // 4. Count
        int count = productDAO.countProducts();
        assertTrue(count >= 15, "Should have at least 15 active products");
    }

    @Test
    @DisplayName("Verify CouponDAO validation and discount calculations")
    public void testCouponDAO() {
        CouponDAO couponDAO = new CouponDAO();
        Optional<Coupon> couponOpt = couponDAO.findByCode("WELCOME10");
        assertTrue(couponOpt.isPresent(), "WELCOME10 coupon should exist");
        Coupon coupon = couponOpt.get();
        assertTrue(coupon.isValid(), "Coupon should be currently active");

        // Order below min order value: 500 < 1000 => discount is 0
        BigDecimal discountLow = coupon.calculateDiscount(new BigDecimal("500.00"));
        assertEquals(BigDecimal.ZERO, discountLow);

        // Order meeting min order value: 2000 => 10% discount = 200.00
        BigDecimal discount = coupon.calculateDiscount(new BigDecimal("2000.00"));
        assertEquals(new BigDecimal("200.00"), discount);
    }
}
