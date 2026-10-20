<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<aside class="admin-sidebar">
    <div style="margin-bottom: 24px; padding-left: 12px;">
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="brand-logo" style="color: #60a5fa;">
            <div class="logo-icon">⚙️</div>
            <div style="color: #ffffff;">Admin<span style="color: #60a5fa;">Hub</span></div>
        </a>
    </div>

    <div class="admin-sidebar-header">Core Management</div>
    <nav>
        <a href="${pageContext.request.contextPath}/admin/dashboard" 
           class="admin-nav-item ${activeTab == 'dashboard' ? 'active' : ''}">
            <span>📊</span>
            <span>Dashboard</span>
        </a>
        <a href="${pageContext.request.contextPath}/admin/products" 
           class="admin-nav-item ${activeTab == 'products' ? 'active' : ''}">
            <span>📦</span>
            <span>Products</span>
        </a>
        <a href="${pageContext.request.contextPath}/admin/categories" 
           class="admin-nav-item ${activeTab == 'categories' ? 'active' : ''}">
            <span>🗂️</span>
            <span>Categories</span>
        </a>
        <a href="${pageContext.request.contextPath}/admin/orders" 
           class="admin-nav-item ${activeTab == 'orders' ? 'active' : ''}">
            <span>📑</span>
            <span>Orders</span>
        </a>
        <a href="${pageContext.request.contextPath}/admin/users" 
           class="admin-nav-item ${activeTab == 'users' ? 'active' : ''}">
            <span>👥</span>
            <span>Customers & Users</span>
        </a>
        <a href="${pageContext.request.contextPath}/admin/coupons" 
           class="admin-nav-item ${activeTab == 'coupons' ? 'active' : ''}">
            <span>🏷️</span>
            <span>Coupons & Deals</span>
        </a>
    </nav>

    <div class="admin-sidebar-header" style="margin-top: 32px;">External</div>
    <nav>
        <a href="${pageContext.request.contextPath}/home" class="admin-nav-item" target="_blank">
            <span>🌐</span>
            <span>View Live Storefront</span>
        </a>
        <a href="${pageContext.request.contextPath}/logout" class="admin-nav-item" style="color: #f87171;">
            <span>🚪</span>
            <span>Logout (${sessionScope.currentUser.name})</span>
        </a>
    </nav>
</aside>
