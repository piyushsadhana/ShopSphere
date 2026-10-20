package com.shopsphere.controller;

import com.shopsphere.model.Coupon;
import com.shopsphere.service.CouponService;
import com.shopsphere.util.CsrfUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

/**
 * Controller for Admin Coupon and Promotion Management.
 */
@WebServlet(name = "AdminCouponServlet", urlPatterns = {"/admin/coupons", "/admin/add-coupon", "/admin/delete-coupon"})
public class AdminCouponServlet extends HttpServlet {

    private final CouponService couponService = new CouponService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Coupon> coupons = couponService.getAllCoupons();
        req.setAttribute("coupons", coupons);

        CsrfUtil.getOrCreateToken(req.getSession(true));
        req.getRequestDispatcher("/admin/coupons.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        if (!CsrfUtil.isValidToken(req)) {
            session.setAttribute("flashError", "Invalid security token.");
            resp.sendRedirect(req.getContextPath() + "/admin/coupons");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) {
            action = "";
        }

        try {
            if ("add".equalsIgnoreCase(action)) {
                String code = req.getParameter("code");
                Coupon.DiscountType type = Coupon.DiscountType.valueOf(req.getParameter("discountType"));
                BigDecimal value = new BigDecimal(req.getParameter("discountValue"));
                BigDecimal minOrder = new BigDecimal(req.getParameter("minOrderValue"));
                int usageLimit = Integer.parseInt(req.getParameter("usageLimit"));
                Date expiryDate = Date.valueOf(req.getParameter("expiryDate"));

                couponService.createCoupon(code, type, value, minOrder, usageLimit, expiryDate);
                session.setAttribute("flashSuccess", "Coupon '" + code.toUpperCase() + "' created successfully!");
            } else if ("delete".equalsIgnoreCase(action)) {
                int couponId = Integer.parseInt(req.getParameter("couponId"));
                couponService.deleteCoupon(couponId);
                session.setAttribute("flashSuccess", "Coupon deleted successfully.");
            }
        } catch (Exception e) {
            session.setAttribute("flashError", e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/coupons");
    }
}
