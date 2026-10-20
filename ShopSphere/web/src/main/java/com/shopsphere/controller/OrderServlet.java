package com.shopsphere.controller;

import com.shopsphere.model.Order;
import com.shopsphere.model.Payment;
import com.shopsphere.model.User;
import com.shopsphere.service.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controller for viewing Orders, Order History, and Order Success confirmation.
 */
@WebServlet(name = "OrderServlet", urlPatterns = {"/orders", "/order-details", "/order-success"})
public class OrderServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?error=auth_required");
            return;
        }

        String uri = req.getRequestURI();

        if (uri.endsWith("/order-success")) {
            handleOrderSuccess(req, resp, user);
        } else if (uri.endsWith("/order-details")) {
            handleOrderDetails(req, resp, user);
        } else {
            handleOrderHistory(req, resp, user);
        }
    }

    private void handleOrderHistory(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        List<Order> orders = orderService.getCustomerOrders(user.getUserId());
        req.setAttribute("orders", orders);
        req.getRequestDispatcher("/orders.jsp").forward(req, resp);
    }

    private void handleOrderDetails(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        String orderIdStr = req.getParameter("id");
        if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/orders");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdStr.trim());
            Optional<Order> orderOpt = orderService.getOrderDetails(orderId);

            if (orderOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/orders");
                return;
            }

            Order order = orderOpt.get();
            // Ownership check (unless admin)
            if (order.getUserId() != user.getUserId() && !user.isAdmin()) {
                resp.sendRedirect(req.getContextPath() + "/orders");
                return;
            }

            Optional<Payment> paymentOpt = orderService.getPaymentForOrder(orderId);

            req.setAttribute("order", order);
            req.setAttribute("payment", paymentOpt.orElse(null));
            req.getRequestDispatcher("/order-details.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/orders");
        }
    }

    private void handleOrderSuccess(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        String orderIdStr = req.getParameter("orderId");
        if (orderIdStr != null && !orderIdStr.trim().isEmpty()) {
            try {
                int orderId = Integer.parseInt(orderIdStr.trim());
                Optional<Order> orderOpt = orderService.getOrderDetails(orderId);
                if (orderOpt.isPresent() && (orderOpt.get().getUserId() == user.getUserId() || user.isAdmin())) {
                    req.setAttribute("order", orderOpt.get());
                }
            } catch (NumberFormatException ignored) {
            }
        }
        req.getRequestDispatcher("/order-success.jsp").forward(req, resp);
    }
}
