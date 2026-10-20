package com.shopsphere.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a discount coupon code in ShopSphere.
 */
public class Coupon implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum DiscountType {
        PERCENTAGE, FIXED
    }

    public enum Status {
        ACTIVE, INACTIVE, EXPIRED
    }

    private int couponId;
    private String code;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private int usageLimit;
    private int usedCount;
    private Date expiryDate;
    private Status status;
    private Timestamp createdAt;

    public Coupon() {
        this.discountType = DiscountType.PERCENTAGE;
        this.discountValue = BigDecimal.ZERO;
        this.minOrderValue = BigDecimal.ZERO;
        this.status = Status.ACTIVE;
        this.usageLimit = 100;
        this.usedCount = 0;
    }

    /**
     * Calculates the discount amount for a given order subtotal.
     * Checks min order value and ensures discount doesn't exceed order subtotal.
     */
    public BigDecimal calculateDiscount(BigDecimal orderSubtotal) {
        if (orderSubtotal == null || orderSubtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (minOrderValue != null && orderSubtotal.compareTo(minOrderValue) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal calculatedDiscount;
        if (DiscountType.PERCENTAGE.equals(discountType)) {
            calculatedDiscount = orderSubtotal.multiply(discountValue)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            calculatedDiscount = discountValue.setScale(2, RoundingMode.HALF_UP);
        }

        // Discount cannot exceed order subtotal
        if (calculatedDiscount.compareTo(orderSubtotal) > 0) {
            return orderSubtotal;
        }
        return calculatedDiscount;
    }

    public boolean isValid() {
        if (!Status.ACTIVE.equals(status)) {
            return false;
        }
        if (usageLimit > 0 && usedCount >= usageLimit) {
            return false;
        }
        if (expiryDate != null) {
            java.util.Date now = new java.util.Date();
            if (expiryDate.before(new Date(now.getTime()))) {
                return false;
            }
        }
        return true;
    }

    public int getCouponId() {
        return couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code != null ? code.trim().toUpperCase() : null;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimal getMinOrderValue() {
        return minOrderValue;
    }

    public void setMinOrderValue(BigDecimal minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    public int getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(int usageLimit) {
        this.usageLimit = usageLimit;
    }

    public int getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
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
}
