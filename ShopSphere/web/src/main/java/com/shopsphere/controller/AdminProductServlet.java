package com.shopsphere.controller;

import com.shopsphere.model.Category;
import com.shopsphere.model.Product;
import com.shopsphere.service.CategoryService;
import com.shopsphere.service.ProductService;
import com.shopsphere.util.CsrfUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Controller for Admin Product Inventory Management (Add, Edit, Delete, Filter).
 */
@WebServlet(name = "AdminProductServlet", urlPatterns = {"/admin/products", "/admin/add-product", "/admin/edit-product", "/admin/delete-product"})
public class AdminProductServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String catParam = req.getParameter("category");
        String searchParam = req.getParameter("q");
        String editIdParam = req.getParameter("editId");

        Integer categoryId = null;
        if (catParam != null && !catParam.trim().isEmpty()) {
            try {
                categoryId = Integer.parseInt(catParam.trim());
            } catch (NumberFormatException ignored) {
            }
        }

        List<Product> products = productService.getAllProductsForAdmin(categoryId, searchParam, null);
        List<Category> categories = categoryService.getAllCategories();

        Product editProduct = null;
        if (editIdParam != null && !editIdParam.trim().isEmpty()) {
            try {
                int editId = Integer.parseInt(editIdParam.trim());
                Optional<Product> pOpt = productService.getProductById(editId);
                editProduct = pOpt.orElse(null);
            } catch (NumberFormatException ignored) {
            }
        }

        req.setAttribute("products", products);
        req.setAttribute("categories", categories);
        req.setAttribute("editProduct", editProduct);
        req.setAttribute("selectedCategory", categoryId);
        req.setAttribute("searchQuery", searchParam);

        CsrfUtil.getOrCreateToken(req.getSession(true));
        req.getRequestDispatcher("/admin/products.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        if (!CsrfUtil.isValidToken(req)) {
            session.setAttribute("flashError", "Invalid security token.");
            resp.sendRedirect(req.getContextPath() + "/admin/products");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) {
            action = "";
        }

        try {
            switch (action) {
                case "add":
                    handleAddProduct(req, session);
                    break;
                case "edit":
                    handleEditProduct(req, session);
                    break;
                case "delete":
                    handleDeleteProduct(req, session);
                    break;
                default:
                    session.setAttribute("flashError", "Invalid action.");
                    break;
            }
        } catch (Exception e) {
            session.setAttribute("flashError", e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/products");
    }

    private void handleAddProduct(HttpServletRequest req, HttpSession session) throws Exception {
        int categoryId = Integer.parseInt(req.getParameter("categoryId"));
        String name = req.getParameter("name");
        String brand = req.getParameter("brand");
        String description = req.getParameter("description");
        BigDecimal price = new BigDecimal(req.getParameter("price"));
        BigDecimal discount = new BigDecimal(req.getParameter("discount"));
        int stock = Integer.parseInt(req.getParameter("stock"));
        String imageUrl = req.getParameter("imageUrl");

        productService.createProduct(categoryId, name, brand, description, price, discount, stock, imageUrl);
        session.setAttribute("flashSuccess", "Product '" + name + "' added successfully!");
    }

    private void handleEditProduct(HttpServletRequest req, HttpSession session) {
        int productId = Integer.parseInt(req.getParameter("productId"));
        int categoryId = Integer.parseInt(req.getParameter("categoryId"));
        String name = req.getParameter("name");
        String brand = req.getParameter("brand");
        String description = req.getParameter("description");
        BigDecimal price = new BigDecimal(req.getParameter("price"));
        BigDecimal discount = new BigDecimal(req.getParameter("discount"));
        int stock = Integer.parseInt(req.getParameter("stock"));
        String imageUrl = req.getParameter("imageUrl");
        String statusStr = req.getParameter("status");
        Product.Status status = Product.Status.valueOf(statusStr != null ? statusStr : "ACTIVE");

        boolean updated = productService.updateProduct(productId, categoryId, name, brand, description, price, discount, stock, imageUrl, status);
        if (updated) {
            session.setAttribute("flashSuccess", "Product updated successfully!");
        } else {
            session.setAttribute("flashError", "Failed to update product.");
        }
    }

    private void handleDeleteProduct(HttpServletRequest req, HttpSession session) {
        int productId = Integer.parseInt(req.getParameter("productId"));
        boolean deleted = productService.deleteProduct(productId);
        if (deleted) {
            session.setAttribute("flashSuccess", "Product deleted or archived successfully.");
        } else {
            session.setAttribute("flashError", "Failed to remove product.");
        }
    }
}
