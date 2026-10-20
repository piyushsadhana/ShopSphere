package com.shopsphere.controller;

import com.shopsphere.model.Category;
import com.shopsphere.service.CategoryService;
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
 * Controller for Admin Category Management (Add, Edit, Delete, Toggle Status).
 */
@WebServlet(name = "AdminCategoryServlet", urlPatterns = {"/admin/categories", "/admin/add-category", "/admin/edit-category", "/admin/delete-category"})
public class AdminCategoryServlet extends HttpServlet {

    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String editIdParam = req.getParameter("editId");
        List<Category> categories = categoryService.getAllCategories();

        Category editCategory = null;
        if (editIdParam != null && !editIdParam.trim().isEmpty()) {
            try {
                int editId = Integer.parseInt(editIdParam.trim());
                Optional<Category> cOpt = categoryService.getCategoryById(editId);
                editCategory = cOpt.orElse(null);
            } catch (NumberFormatException ignored) {
            }
        }

        req.setAttribute("categories", categories);
        req.setAttribute("editCategory", editCategory);

        CsrfUtil.getOrCreateToken(req.getSession(true));
        req.getRequestDispatcher("/admin/categories.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        if (!CsrfUtil.isValidToken(req)) {
            session.setAttribute("flashError", "Invalid security token.");
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) {
            action = "";
        }

        try {
            switch (action) {
                case "add":
                    String name = req.getParameter("name");
                    String desc = req.getParameter("description");
                    String img = req.getParameter("imageUrl");
                    categoryService.createCategory(name, desc, img);
                    session.setAttribute("flashSuccess", "Category '" + name + "' added successfully!");
                    break;

                case "edit":
                    int catId = Integer.parseInt(req.getParameter("categoryId"));
                    String editName = req.getParameter("name");
                    String editDesc = req.getParameter("description");
                    String editImg = req.getParameter("imageUrl");
                    String statusStr = req.getParameter("status");
                    Category.Status status = Category.Status.valueOf(statusStr != null ? statusStr : "ACTIVE");
                    categoryService.updateCategory(catId, editName, editDesc, editImg, status);
                    session.setAttribute("flashSuccess", "Category updated successfully!");
                    break;

                case "delete":
                    int deleteId = Integer.parseInt(req.getParameter("categoryId"));
                    boolean deleted = categoryService.deleteCategory(deleteId);
                    if (deleted) {
                        session.setAttribute("flashSuccess", "Category deleted or archived successfully.");
                    } else {
                        session.setAttribute("flashError", "Failed to remove category.");
                    }
                    break;

                default:
                    session.setAttribute("flashError", "Invalid category action.");
                    break;
            }
        } catch (Exception e) {
            session.setAttribute("flashError", e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/categories");
    }
}
