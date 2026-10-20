package com.shopsphere.service;

import com.shopsphere.dao.OrderDAO;
import com.shopsphere.dao.OrderItemDAO;
import com.shopsphere.dao.PaymentDAO;
import com.shopsphere.model.Order;
import com.shopsphere.model.OrderItem;
import com.shopsphere.model.Payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service managing order lookups, customer order history, and admin order lifecycle.
 */
public class OrderService {

    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

    public Optional<Order> getOrderDetails(int orderId) {
        Optional<Order> orderOpt = orderDAO.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            List<OrderItem> items = orderItemDAO.findByOrderId(orderId);
            order.setItems(items);
            return Optional.of(order);
        }
        return Optional.empty();
    }

    public Optional<Order> getOrderByNumber(String orderNumber) {
        Optional<Order> orderOpt = orderDAO.findByOrderNumber(orderNumber);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            List<OrderItem> items = orderItemDAO.findByOrderId(order.getOrderId());
            order.setItems(items);
            return Optional.of(order);
        }
        return Optional.empty();
    }

    public List<Order> getCustomerOrders(int userId) {
        List<Order> orders = orderDAO.findByUserId(userId);
        for (Order o : orders) {
            o.setItems(orderItemDAO.findByOrderId(o.getOrderId()));
        }
        return orders;
    }

    public List<Order> getAllOrdersForAdmin(String statusFilter) {
        List<Order> orders = orderDAO.findAll(statusFilter);
        for (Order o : orders) {
            o.setItems(orderItemDAO.findByOrderId(o.getOrderId()));
        }
        return orders;
    }

    public boolean updateStatus(int orderId, Order.OrderStatus orderStatus, Order.PaymentStatus paymentStatus) {
        return orderDAO.updateStatus(orderId, orderStatus, paymentStatus);
    }

    public Optional<Payment> getPaymentForOrder(int orderId) {
        return paymentDAO.findByOrderId(orderId);
    }

    public int countTotalOrders() {
        return orderDAO.countOrders();
    }

    public BigDecimal getTotalRevenue() {
        return orderDAO.getTotalRevenue();
    }
}
