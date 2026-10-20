package com.shopsphere.service;

import com.shopsphere.dao.AddressDAO;
import com.shopsphere.dao.ProductDAO;
import com.shopsphere.model.Address;
import com.shopsphere.model.CartItem;
import com.shopsphere.model.Order;
import com.shopsphere.model.Product;
import com.shopsphere.model.Review;
import com.shopsphere.model.User;
import com.shopsphere.util.DBConnection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ECommerceIntegrationTest {

    private static UserService userService;
    private static ProductService productService;
    private static CartService cartService;
    private static CheckoutService checkoutService;
    private static OrderService orderService;
    private static WishlistService wishlistService;
    private static ReviewService reviewService;
    private static AddressDAO addressDAO;

    private static int testUserId;
    private static int testOrderId;

    @BeforeAll
    public static void setUp() {
        assertTrue(DBConnection.testConnection(), "Database connection must be active");
        userService = new UserService();
        productService = new ProductService();
        cartService = new CartService();
        checkoutService = new CheckoutService();
        orderService = new OrderService();
        wishlistService = new WishlistService();
        reviewService = new ReviewService();
        addressDAO = new AddressDAO();
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("1. Customer Registration & Authentication")
    public void testCustomerRegistrationAndLogin() throws Exception {
        String uniqueEmail = "test_shopper_" + System.currentTimeMillis() + "@example.com";
        User user = userService.register("Integration Shopper", uniqueEmail, "Shopper@123", "+91 9999988888");

        assertNotNull(user);
        assertTrue(user.getUserId() > 0);
        testUserId = user.getUserId();

        // Test login
        Optional<User> loggedInOpt = userService.authenticate(uniqueEmail, "Shopper@123");
        assertTrue(loggedInOpt.isPresent());
        assertEquals("Integration Shopper", loggedInOpt.get().getName());

        // Test wrong credentials
        Optional<User> failedOpt = userService.authenticate(uniqueEmail, "WrongPassword");
        assertTrue(failedOpt.isEmpty());
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("2. Cart Operations & Stock Validation")
    public void testCartOperations() {
        // Add Product 1 (headphones) to cart
        boolean added = cartService.addToCart(testUserId, 1, 2);
        assertTrue(added);

        List<CartItem> cart = cartService.getCartForUser(testUserId);
        assertFalse(cart.isEmpty());
        assertEquals(1, cart.size());
        assertEquals(2, cart.get(0).getQuantity());

        // Test subtotal calculation
        BigDecimal subtotal = cartService.calculateSubtotal(cart);
        assertTrue(subtotal.compareTo(BigDecimal.ZERO) > 0);

        // Update quantity
        cartService.updateQuantity(testUserId, 1, 3);
        List<CartItem> updatedCart = cartService.getCartForUser(testUserId);
        assertEquals(3, updatedCart.get(0).getQuantity());
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    @DisplayName("3. Transactional Checkout & Inventory Deduction")
    public void testTransactionalCheckout() throws Exception {
        // Create delivery address for test user
        Address address = new Address();
        address.setUserId(testUserId);
        address.setRecipientName("Integration Shopper");
        address.setPhone("+91 9999988888");
        address.setAddressLine1("Plot 101, Innovation Block");
        address.setCity("Bengaluru");
        address.setState("Karnataka");
        address.setPostalCode("560001");
        address.setCountry("India");
        address.setDefault(true);
        address = addressDAO.create(address);

        // Check stock before checkout
        ProductDAO pDAO = new ProductDAO();
        Product beforeProduct = pDAO.findById(1).orElseThrow();
        int initialStock = beforeProduct.getStock();

        // Place order with coupon WELCOME10
        Order placedOrder = checkoutService.placeOrder(
                testUserId,
                address,
                "Credit / Debit Card (Demo)",
                "WELCOME10",
                "Leave at front desk"
        );

        assertNotNull(placedOrder);
        assertTrue(placedOrder.getOrderId() > 0);
        testOrderId = placedOrder.getOrderId();

        // Verify stock deducted by ordered quantity (3 units)
        Product afterProduct = pDAO.findById(1).orElseThrow();
        assertEquals(initialStock - 3, afterProduct.getStock(), "Stock should decrease by ordered quantity");

        // Verify cart cleared
        List<CartItem> cartAfter = cartService.getCartForUser(testUserId);
        assertTrue(cartAfter.isEmpty(), "Cart should be cleared after order placement");
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    @DisplayName("4. Order Tracking & Lifecycle Status Update")
    public void testOrderLifecycle() {
        Optional<Order> orderOpt = orderService.getOrderDetails(testOrderId);
        assertTrue(orderOpt.isPresent());
        Order order = orderOpt.get();
        assertEquals("Credit / Debit Card (Demo)", order.getPaymentMethod());
        assertFalse(order.getItems().isEmpty());

        // Update status as Admin (SHIPPED -> DELIVERED)
        boolean updated = orderService.updateStatus(testOrderId, Order.OrderStatus.SHIPPED, Order.PaymentStatus.PAID);
        assertTrue(updated);

        Order updatedOrder = orderService.getOrderDetails(testOrderId).orElseThrow();
        assertEquals(Order.OrderStatus.SHIPPED, updatedOrder.getOrderStatus());
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    @DisplayName("5. Wishlist Operations")
    public void testWishlist() {
        // Add Product 3 to wishlist
        boolean added = wishlistService.addToWishlist(testUserId, 3);
        assertTrue(added);
        assertTrue(wishlistService.isInWishlist(testUserId, 3));

        // Duplicate add ignored safely
        wishlistService.addToWishlist(testUserId, 3);
        assertEquals(1, wishlistService.getWishlistCount(testUserId));

        // Remove
        boolean removed = wishlistService.removeFromWishlist(testUserId, 3);
        assertTrue(removed);
        assertFalse(wishlistService.isInWishlist(testUserId, 3));
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    @DisplayName("6. Verified Review Submission")
    public void testReviewSubmission() throws Exception {
        Review review = reviewService.submitReview(
                testUserId,
                1,
                5,
                "Spectacular sound quality and battery life! Highly recommended."
        );

        assertNotNull(review);
        assertTrue(review.getReviewId() > 0);
        assertTrue(reviewService.hasUserReviewed(testUserId, 1));

        List<Review> reviews = reviewService.getReviewsForProduct(1);
        assertFalse(reviews.isEmpty());
    }
}
