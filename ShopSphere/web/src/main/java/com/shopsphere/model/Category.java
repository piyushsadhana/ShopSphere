package com.shopsphere.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Represents a product category.
 */
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        ACTIVE, INACTIVE
    }

    private int categoryId;
    private String name;
    private String description;
    private String imageUrl;
    private Status status;
    private Timestamp createdAt;
    private int productCount; // Transient helper for dashboard/listings

    public Category() {
        this.status = Status.ACTIVE;
    }

    public Category(int categoryId, String name, String description, String imageUrl, Status status) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.status = status != null ? status : Status.ACTIVE;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public boolean isActive() {
        return Status.ACTIVE.equals(this.status);
    }
}
