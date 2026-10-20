package com.shopsphere.service;

import com.shopsphere.dao.CategoryDAO;
import com.shopsphere.model.Category;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service for Category business operations.
 */
public class CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDAO();
    }

    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public List<Category> getActiveCategories() {
        return categoryDAO.findAll(true);
    }

    public List<Category> getAllCategories() {
        return categoryDAO.findAll(false);
    }

    public Optional<Category> getCategoryById(int categoryId) {
        return categoryDAO.findById(categoryId);
    }

    public Category createCategory(String name, String description, String imageUrl) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required.");
        }
        Category c = new Category();
        c.setName(name.trim());
        c.setDescription(description != null ? description.trim() : "");
        c.setImageUrl(imageUrl != null ? imageUrl.trim() : "images/cat-default.svg");
        c.setStatus(Category.Status.ACTIVE);
        return categoryDAO.create(c);
    }

    public boolean updateCategory(int categoryId, String name, String description, String imageUrl, Category.Status status) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required.");
        }
        Category c = new Category();
        c.setCategoryId(categoryId);
        c.setName(name.trim());
        c.setDescription(description != null ? description.trim() : "");
        c.setImageUrl(imageUrl != null ? imageUrl.trim() : "images/cat-default.svg");
        c.setStatus(status != null ? status : Category.Status.ACTIVE);
        return categoryDAO.update(c);
    }

    public boolean deleteCategory(int categoryId) {
        return categoryDAO.delete(categoryId);
    }

    public int countActiveCategories() {
        return categoryDAO.countCategories();
    }
}
