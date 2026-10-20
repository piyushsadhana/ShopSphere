<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<footer class="footer">
    <div class="container footer-grid">
        <div class="footer-col">
            <div class="brand-logo" style="margin-bottom: 16px;">
                <div class="logo-icon">🛍️</div>
                <div style="color: #ffffff;">Shop<span style="color: #60a5fa;">Sphere</span></div>
            </div>
            <p style="font-size: 14px; line-height: 1.6; color: #94a3b8; margin-bottom: 16px;">
                ShopSphere is a next-generation full-stack Java e-commerce platform built with Jakarta Servlets, JSP, JSTL, and MySQL. Designed for speed, reliability, and security.
            </p>
            <p style="font-size: 13px; color: #cbd5e1;">📍 Tech Innovation Hub, Bengaluru, India</p>
        </div>

        <div class="footer-col">
            <h4>Quick Links</h4>
            <ul>
                <li><a href="${pageContext.request.contextPath}/home">Home</a></li>
                <li><a href="${pageContext.request.contextPath}/products">All Products</a></li>
                <li><a href="${pageContext.request.contextPath}/cart">Shopping Cart</a></li>
                <li><a href="${pageContext.request.contextPath}/wishlist">My Wishlist</a></li>
                <li><a href="${pageContext.request.contextPath}/orders">Track Orders</a></li>
            </ul>
        </div>

        <div class="footer-col">
            <h4>Customer Support</h4>
            <ul>
                <li><a href="${pageContext.request.contextPath}/profile">My Account & Addresses</a></li>
                <li><a href="#">Shipping & Delivery Policy</a></li>
                <li><a href="#">Returns & Refund Guarantee</a></li>
                <li><a href="#">Payment Methods & Security</a></li>
                <li><a href="#">Frequently Asked Questions</a></li>
            </ul>
        </div>

        <div class="footer-col">
            <h4>Demo Credentials</h4>
            <div style="background-color: #1e293b; padding: 12px; border-radius: 8px; font-size: 12px; line-height: 1.7;">
                <p><strong style="color: #60a5fa;">Admin:</strong> admin@shopsphere.com</p>
                <p>Pass: <code style="color: #fcd34d;">Admin@123</code></p>
                <div style="height: 1px; background: #334155; margin: 6px 0;"></div>
                <p><strong style="color: #34d399;">Customer:</strong> john@example.com</p>
                <p>Pass: <code style="color: #fcd34d;">Customer@123</code></p>
            </div>
        </div>
    </div>

    <div class="container footer-bottom">
        <div>
            &copy; 2026 ShopSphere E-Commerce. All rights reserved.
        </div>
        <div style="display: flex; gap: 20px;">
            <a href="#" style="color: #94a3b8;">Privacy Policy</a>
            <a href="#" style="color: #94a3b8;">Terms of Service</a>
            <a href="#" style="color: #94a3b8;">Security</a>
        </div>
    </div>
</footer>

<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
