package com.shopsphere.service;

import com.shopsphere.dao.CartDAO;
import com.shopsphere.dao.CouponDAO;
import com.shopsphere.dao.OrderDAO;
import com.shopsphere.dao.OrderItemDAO;
import com.shopsphere.dao.PaymentDAO;
import com.shopsphere.dao.ProductDAO;
import com.shopsphere.model.Address;
import com.shopsphere.model.CartItem;
import com.shopsphere.model.Coupon;
import com.shopsphere.model.Order;
import com.shopsphere.model.OrderItem;
import com.shopsphere.model.Payment;
import com.shopsphere.model.Product;
import com.shopsphere.util.DBConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service managing transactional order placement, stock verification,
 * discount recalculation, and payment generation.
 */
public class CheckoutService {

    private static final Logger LOGGER = Logger.getLogger(CheckoutService.class.getName());

    private final ProductDAO productDAO = new ProductDAO();
    private final CartDAO cartDAO = new CartDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final CouponDAO couponDAO = new CouponDAO();

    /**
     * Executes the complete checkout process inside a single ACID database transaction.
     *
     * @param userId customer user ID
     * @param shippingAddress full address object to snapshot
     * @param paymentMethod chosen payment method (COD, Card Demo, UPI Demo)
     * @param couponCode optional coupon code applied
     * @param orderNotes optional delivery instructions
     * @return placed Order object
     * @throws Exception if validation or database transaction fails
     */
    public Order placeOrder(int userId, Address shippingAddress, String paymentMethod,
                            String couponCode, String orderNotes) throws Exception {
        if (shippingAddress == null) {
            throw new IllegalArgumentException("A valid delivery address must be selected.");
        }
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("A valid payment method must be selected.");
        }

        List<CartItem> cartItems = cartDAO.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Your shopping cart is currently empty.");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin ACID Transaction

            // 1. Re-validate stock for every item and calculate subtotal
            BigDecimal subtotal = BigDecimal.ZERO;
            for (CartItem item : cartItems) {
                Optional<Product> prodOpt = productDAO.findById(item.getProductId());
                if (prodOpt.isEmpty() || !prodOpt.get().isActive()) {
                    throw new IllegalStateException("Product '" + item.getProduct().getName() + "' is no longer available.");
                }
                Product p = prodOpt.get();
                if (p.getStock() < item.getQuantity()) {
                    throw new IllegalStateException("Insufficient stock for '" + p.getName() + "'. Available: " + p.getStock() + " units.");
                }

                BigDecimal itemPrice = p.getFinalPrice();
                BigDecimal itemTotal = itemPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                subtotal = subtotal.add(itemTotal);
            }

            // 2. Validate and apply coupon if provided
            BigDecimal discountAmount = BigDecimal.ZERO;
            Integer couponIdToIncrement = null;
            if (couponCode != null && !couponCode.trim().isEmpty()) {
                Optional<Coupon> cOpt = couponDAO.findByCode(couponCode.trim());
                if (cOpt.isPresent() && cOpt.get().isValid()) {
                    Coupon coupon = cOpt.get();
                    if (coupon.getMinOrderValue() == null || subtotal.compareTo(coupon.getMinOrderValue()) >= 0) {
                        discountAmount = coupon.calculateDiscount(subtotal);
                        couponIdToIncrement = coupon.getCouponId();
                    }
                }
            }

            // 3. Calculate taxes and shipping
            BigDecimal taxableAmount = subtotal.subtract(discountAmount);
            if (taxableAmount.compareTo(BigDecimal.ZERO) < 0) {
                taxableAmount = BigDecimal.ZERO;
            }

            // 18% GST calculation
            BigDecimal taxAmount = taxableAmount.multiply(BigDecimal.valueOf(0.18)).setScale(2, RoundingMode.HALF_UP);

            // Free shipping if order > 1000, else 99
            BigDecimal shippingFee = (subtotal.compareTo(BigDecimal.valueOf(1000)) >= 0)
                    ? BigDecimal.ZERO : BigDecimal.valueOf(99.00);

            BigDecimal finalAmount = taxableAmount.add(taxAmount).add(shippingFee).setScale(2, RoundingMode.HALF_UP);

            // 4. Generate unique order number
            String orderNumber = "ORD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                    + "-" + (int)(Math.random() * 900 + 100);

            // 5. Build and save Order record
            Order order = new Order();
            order.setOrderNumber(orderNumber);
            order.setUserId(userId);
            order.setAddressSnapshot(shippingAddress.getFormattedAddress());
            order.setTotalAmount(subtotal);
            order.setDiscountAmount(discountAmount);
            order.setShippingFee(shippingFee);
            order.setTaxAmount(taxAmount);
            order.setFinalAmount(finalAmount);
            order.setOrderStatus(Order.OrderStatus.PENDING);

            boolean isCod = paymentMethod.toLowerCase().contains("cash") || paymentMethod.toLowerCase().contains("cod");
            order.setPaymentStatus(isCod ? Order.PaymentStatus.PENDING : Order.PaymentStatus.PAID);
            order.setPaymentMethod(paymentMethod);
            order.setCouponCode(couponCode);
            order.setNotes(orderNotes);

            order = orderDAO.create(order, conn);

            // 6. Create OrderItems and safely decrement inventory
            for (CartItem item : cartItems) {
                Product p = item.getProduct();
                BigDecimal unitPrice = p.getFinalPrice();
                BigDecimal lineSubtotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getOrderId());
                orderItem.setProductId(p.getProductId());
                orderItem.setProductName(p.getName());
                orderItem.setProductPrice(unitPrice);
                orderItem.setQuantity(item.getQuantity());
                orderItem.setSubtotal(lineSubtotal);

                orderItemDAO.create(orderItem, conn);
                order.addItem(orderItem);

                // Inventory decrement within transaction
                boolean stockReduced = productDAO.reduceStock(conn, p.getProductId(), item.getQuantity());
                if (!stockReduced) {
                    throw new IllegalStateException("Failed to reserve stock for '" + p.getName() + "'. Order aborted.");
                }
            }

            // 7. Increment coupon usage count if used
            if (couponIdToIncrement != null) {
                couponDAO.incrementUsage(couponIdToIncrement, conn);
            }

            // 8. Create Payment record
            Payment payment = new Payment();
            payment.setOrderId(order.getOrderId());
            payment.setAmount(finalAmount);
            payment.setMethod(paymentMethod);
            payment.setStatus(isCod ? Payment.Status.PENDING : Payment.Status.SUCCESS);
            payment.setTransactionRef(isCod ? "COD-PENDING" : "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
            paymentDAO.create(payment, conn);

            // 9. Clear user's cart in database
            cartDAO.clearCart(userId, conn);

            conn.commit(); // Commit Transaction!
            LOGGER.info("Order " + orderNumber + " placed successfully for user ID " + userId);
            return order;

        } catch (Exception ex) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback Transaction on error!
                    LOGGER.warning("Transaction rolled back for checkout: " + ex.getMessage());
                } catch (SQLException rollbackEx) {
                    LOGGER.log(Level.SEVERE, "Failed to rollback checkout transaction", rollbackEx);
                }
            }
            throw ex;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }
}
