package com.shopsphere.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Represents a registered user in ShopSphere (CUSTOMER or ADMIN).
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Role {
        CUSTOMER, ADMIN
    }

    public enum Status {
        ACTIVE, INACTIVE, SUSPENDED
    }

    private int userId;
    private String name;
    private String email;
    private String password; // BCrypt hash
    private String phone;
    private Role role;
    private Status status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public User() {
        this.role = Role.CUSTOMER;
        this.status = Status.ACTIVE;
    }

    public User(int userId, String name, String email, String password, String phone, Role role, Status status) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role != null ? role : Role.CUSTOMER;
        this.status = status != null ? status : Status.ACTIVE;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    public boolean isAdmin() {
        return Role.ADMIN.equals(this.role);
    }

    public boolean isActive() {
        return Status.ACTIVE.equals(this.status);
    }
}
