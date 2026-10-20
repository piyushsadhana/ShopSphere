package com.shopsphere.service;

import com.shopsphere.dao.CouponDAO;
import com.shopsphere.model.Coupon;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service managing Coupons and promotional discounts.
 */
public class CouponService {

    private final CouponDAO couponDAO;

    public CouponService() {
        this.couponDAO = new CouponDAO();
    }

    public CouponService(CouponDAO couponDAO) {
        this.couponDAO = couponDAO;
    }

    public Optional<Coupon> findByCode(String code) {
        return couponDAO.findByCode(code);
    }

    public Optional<Coupon> validateCoupon(String code, BigDecimal orderSubtotal) throws IllegalArgumentException {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter a valid coupon code.");
        }
        Optional<Coupon> opt = couponDAO.findByCode(code.trim().toUpperCase());
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Coupon code '" + code + "' does not exist.");
        }
        Coupon coupon = opt.get();
        if (!coupon.isValid()) {
            throw new IllegalArgumentException("This coupon has expired or reached its maximum usage limit.");
        }
        if (coupon.getMinOrderValue() != null && orderSubtotal.compareTo(coupon.getMinOrderValue()) < 0) {
            throw new IllegalArgumentException("Minimum order value of \u20B9" + coupon.getMinOrderValue() + " required to use this coupon.");
        }
        return Optional.of(coupon);
    }

    public List<Coupon> getAllCoupons() {
        return couponDAO.findAll();
    }

    public Coupon createCoupon(String code, Coupon.DiscountType type, BigDecimal value,
                               BigDecimal minOrder, int usageLimit, Date expiryDate) throws SQLException {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Coupon code is required.");
        }
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Discount value must be greater than zero.");
        }
        if (expiryDate == null) {
            throw new IllegalArgumentException("Expiry date is required.");
        }

        Coupon c = new Coupon();
        c.setCode(code.trim().toUpperCase());
        c.setDiscountType(type != null ? type : Coupon.DiscountType.PERCENTAGE);
        c.setDiscountValue(value);
        c.setMinOrderValue(minOrder != null ? minOrder : BigDecimal.ZERO);
        c.setUsageLimit(usageLimit > 0 ? usageLimit : 100);
        c.setUsedCount(0);
        c.setExpiryDate(expiryDate);
        c.setStatus(Coupon.Status.ACTIVE);

        return couponDAO.create(c);
    }

    public boolean updateCoupon(int couponId, String code, Coupon.DiscountType type, BigDecimal value,
                                BigDecimal minOrder, int usageLimit, Date expiryDate, Coupon.Status status) {
        Coupon c = new Coupon();
        c.setCouponId(couponId);
        c.setCode(code.trim().toUpperCase());
        c.setDiscountType(type);
        c.setDiscountValue(value);
        c.setMinOrderValue(minOrder != null ? minOrder : BigDecimal.ZERO);
        c.setUsageLimit(usageLimit);
        c.setExpiryDate(expiryDate);
        c.setStatus(status != null ? status : Coupon.Status.ACTIVE);

        return couponDAO.update(c);
    }

    public boolean deleteCoupon(int couponId) {
        return couponDAO.delete(couponId);
    }
}
