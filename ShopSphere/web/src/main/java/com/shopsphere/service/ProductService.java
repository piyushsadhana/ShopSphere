package com.shopsphere.service;

import com.shopsphere.dao.ProductDAO;
import com.shopsphere.model.Product;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service for Product catalog business operations.
 */
public class ProductService {

    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
    }

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public Optional<Product> getProductById(int productId) {
        return productDAO.findById(productId);
    }

    public List<Product> getFeaturedProducts(int limit) {
        return productDAO.findFeatured(limit);
    }

    public List<Product> getActiveProducts(Integer categoryId, String search,
                                          BigDecimal minPrice, BigDecimal maxPrice, String sortBy) {
        return productDAO.searchAndFilter(true, categoryId, search, minPrice, maxPrice, sortBy);
    }

    public List<Product> getAllProductsForAdmin(Integer categoryId, String search, String sortBy) {
        return productDAO.searchAndFilter(false, categoryId, search, null, null, sortBy);
    }

    public Product createProduct(int categoryId, String name, String brand, String description,
                                 BigDecimal price, BigDecimal discount, int stock, String imageUrl) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valid product price is required.");
        }
        if (discount == null || discount.compareTo(BigDecimal.ZERO) < 0 || discount.compareTo(BigDecimal.valueOf(100)) > 0) {
            discount = BigDecimal.ZERO;
        }
        if (stock < 0) {
            stock = 0;
        }

        Product p = new Product();
        p.setCategoryId(categoryId);
        p.setName(name.trim());
        p.setBrand(brand != null ? brand.trim() : "");
        p.setDescription(description != null ? description.trim() : "");
        p.setPrice(price);
        p.setDiscount(discount);
        p.setStock(stock);
        p.setImageUrl(imageUrl != null && !imageUrl.trim().isEmpty() ? imageUrl.trim() : "images/products/placeholder.svg");
        p.setStatus(Product.Status.ACTIVE);

        return productDAO.create(p);
    }

    public boolean updateProduct(int productId, int categoryId, String name, String brand,
                                 String description, BigDecimal price, BigDecimal discount,
                                 int stock, String imageUrl, Product.Status status) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valid product price is required.");
        }

        Product p = new Product();
        p.setProductId(productId);
        p.setCategoryId(categoryId);
        p.setName(name.trim());
        p.setBrand(brand != null ? brand.trim() : "");
        p.setDescription(description != null ? description.trim() : "");
        p.setPrice(price);
        p.setDiscount(discount != null ? discount : BigDecimal.ZERO);
        p.setStock(stock >= 0 ? stock : 0);
        p.setImageUrl(imageUrl != null && !imageUrl.trim().isEmpty() ? imageUrl.trim() : "images/products/placeholder.svg");
        p.setStatus(status != null ? status : Product.Status.ACTIVE);

        return productDAO.update(p);
    }

    public boolean deleteProduct(int productId) {
        return productDAO.delete(productId);
    }

    public int countActiveProducts() {
        return productDAO.countProducts();
    }
}
