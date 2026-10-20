package com.shopsphere.controller;

import com.shopsphere.model.Category;
import com.shopsphere.model.Product;
import com.shopsphere.service.CategoryService;
import com.shopsphere.service.ProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for Product Catalog listing, filtering, and search.
 */
@WebServlet(name = "ProductServlet", urlPatterns = {"/products"})
public class ProductServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String catParam = req.getParameter("category");
        String searchParam = req.getParameter("q");
        String minPriceParam = req.getParameter("minPrice");
        String maxPriceParam = req.getParameter("maxPrice");
        String sortParam = req.getParameter("sort");

        Integer categoryId = null;
        if (catParam != null && !catParam.trim().isEmpty()) {
            try {
                categoryId = Integer.parseInt(catParam.trim());
            } catch (NumberFormatException ignored) {
            }
        }

        BigDecimal minPrice = null;
        if (minPriceParam != null && !minPriceParam.trim().isEmpty()) {
            try {
                minPrice = new BigDecimal(minPriceParam.trim());
            } catch (Exception ignored) {
            }
        }

        BigDecimal maxPrice = null;
        if (maxPriceParam != null && !maxPriceParam.trim().isEmpty()) {
            try {
                maxPrice = new BigDecimal(maxPriceParam.trim());
            } catch (Exception ignored) {
            }
        }

        List<Product> products = productService.getActiveProducts(categoryId, searchParam, minPrice, maxPrice, sortParam);
        List<Category> categories = categoryService.getActiveCategories();

        req.setAttribute("products", products);
        req.setAttribute("categories", categories);
        req.setAttribute("selectedCategory", categoryId);
        req.setAttribute("searchQuery", searchParam);
        req.setAttribute("minPrice", minPriceParam);
        req.setAttribute("maxPrice", maxPriceParam);
        req.setAttribute("selectedSort", sortParam);

        req.getRequestDispatcher("/products.jsp").forward(req, resp);
    }
}
