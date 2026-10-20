package com.shopsphere.controller;

import com.shopsphere.model.Order;
import com.shopsphere.model.User;
import com.shopsphere.service.CategoryService;
import com.shopsphere.service.OrderService;
import com.shopsphere.service.ProductService;
import com.shopsphere.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for Admin Control Center & Analytics Dashboard.
 */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin", "/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();
    private final UserService userService = new UserService();
    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int productCount = productService.countActiveProducts();
        int categoryCount = categoryService.countActiveCategories();
        int customerCount = userService.countCustomers();
        int orderCount = orderService.countTotalOrders();
        BigDecimal totalRevenue = orderService.getTotalRevenue();

        List<Order> recentOrders = orderService.getAllOrdersForAdmin(null);
        if (recentOrders.size() > 5) {
            recentOrders = recentOrders.subList(0, 5);
        }

        List<User> recentUsers = userService.getAllUsers();
        if (recentUsers.size() > 5) {
            recentUsers = recentUsers.subList(0, 5);
        }

        req.setAttribute("productCount", productCount);
        req.setAttribute("categoryCount", categoryCount);
        req.setAttribute("customerCount", customerCount);
        req.setAttribute("orderCount", orderCount);
        req.setAttribute("totalRevenue", totalRevenue);
        req.setAttribute("recentOrders", recentOrders);
        req.setAttribute("recentUsers", recentUsers);

        req.getRequestDispatcher("/admin/dashboard.jsp").forward(req, resp);
    }
}
