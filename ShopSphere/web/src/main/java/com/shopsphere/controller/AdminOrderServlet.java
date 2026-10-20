package com.shopsphere.controller;

import com.shopsphere.model.Order;
import com.shopsphere.service.OrderService;
import com.shopsphere.util.CsrfUtil;
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
 * Controller for Admin Order Management and Lifecycle Tracking.
 */
@WebServlet(name = "AdminOrderServlet", urlPatterns = {"/admin/orders", "/admin/update-order"})
public class AdminOrderServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String statusFilter = req.getParameter("status");
        String viewIdStr = req.getParameter("viewId");

        List<Order> orders = orderService.getAllOrdersForAdmin(statusFilter);

        Order viewOrder = null;
        if (viewIdStr != null && !viewIdStr.trim().isEmpty()) {
            try {
                int viewId = Integer.parseInt(viewIdStr.trim());
                Optional<Order> oOpt = orderService.getOrderDetails(viewId);
                viewOrder = oOpt.orElse(null);
            } catch (NumberFormatException ignored) {
            }
        }

        req.setAttribute("orders", orders);
        req.setAttribute("viewOrder", viewOrder);
        req.setAttribute("selectedStatus", statusFilter);

        CsrfUtil.getOrCreateToken(req.getSession(true));
        req.getRequestDispatcher("/admin/orders.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        if (!CsrfUtil.isValidToken(req)) {
            session.setAttribute("flashError", "Invalid security token.");
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
            return;
        }

        try {
            int orderId = Integer.parseInt(req.getParameter("orderId"));
            String orderStatusStr = req.getParameter("orderStatus");
            String paymentStatusStr = req.getParameter("paymentStatus");

            Order.OrderStatus orderStatus = null;
            if (orderStatusStr != null && !orderStatusStr.isEmpty()) {
                orderStatus = Order.OrderStatus.valueOf(orderStatusStr);
            }

            Order.PaymentStatus paymentStatus = null;
            if (paymentStatusStr != null && !paymentStatusStr.isEmpty()) {
                paymentStatus = Order.PaymentStatus.valueOf(paymentStatusStr);
            }

            boolean updated = orderService.updateStatus(orderId, orderStatus, paymentStatus);
            if (updated) {
                session.setAttribute("flashSuccess", "Order #" + orderId + " status updated!");
            } else {
                session.setAttribute("flashError", "Failed to update order status.");
            }
        } catch (Exception e) {
            session.setAttribute("flashError", e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/orders");
    }
}
