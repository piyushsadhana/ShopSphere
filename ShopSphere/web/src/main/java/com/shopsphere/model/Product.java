package com.shopsphere.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

/**
 * Represents a product in the ShopSphere catalog.
 */
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        ACTIVE, INACTIVE, ARCHIVED
    }

    private int productId;
    private int categoryId;
    private String categoryName; // Helper for display
    private String name;
    private String brand;
    private String description;
    private BigDecimal price;
    private BigDecimal discount; // Percentage, e.g. 15.00
    private int stock;
    private String imageUrl;
    private Status status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Transient fields for customer experience
    private double averageRating;
    private int reviewCount;

    public Product() {
        this.price = BigDecimal.ZERO;
        this.discount = BigDecimal.ZERO;
        this.stock = 0;
        this.status = Status.ACTIVE;
    }

    public Product(int productId, int categoryId, String name, String brand, String description,
                   BigDecimal price, BigDecimal discount, int stock, String imageUrl, Status status) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.name = name;
        this.brand = brand;
        this.description = description;
        this.price = price != null ? price : BigDecimal.ZERO;
        this.discount = discount != null ? discount : BigDecimal.ZERO;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.status = status != null ? status : Status.ACTIVE;
    }

    /**
     * Calculates the discounted final selling price:
     * price * (100 - discount) / 100
     */
    public BigDecimal getFinalPrice() {
        if (price == null) {
            return BigDecimal.ZERO;
        }
        if (discount == null || discount.compareTo(BigDecimal.ZERO) <= 0) {
            return price.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal discountFactor = BigDecimal.valueOf(100).subtract(discount);
        return price.multiply(discountFactor).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the exact amount saved by the customer.
     */
    public BigDecimal getSavings() {
        if (price == null || discount == null || discount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return price.subtract(getFinalPrice()).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isInStock() {
        return stock > 0;
    }

    public boolean isActive() {
        return Status.ACTIVE.equals(this.status);
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }
}
