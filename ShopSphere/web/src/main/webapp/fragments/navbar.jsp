<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- Top Info Bar -->
<div class="top-bar">
    <div class="container top-bar-inner">
        <div>
            <span>⚡ Free Express Delivery on orders over ₹1,000 | 100% Secure Checkout</span>
        </div>
        <div class="top-bar-links">
            <c:if test="${sessionScope.currentUser != null && sessionScope.currentUser.admin}">
                <a href="${pageContext.request.contextPath}/admin/dashboard" style="color: #60a5fa; font-weight: 700;">⚙️ Admin Control Panel</a>
            </c:if>
            <a href="${pageContext.request.contextPath}/orders">📦 Track Orders</a>
            <a href="${pageContext.request.contextPath}/profile">👤 My Account</a>
        </div>
    </div>
</div>

<!-- Main Sticky Navigation Bar -->
<header class="navbar">
    <div class="container navbar-inner">
        <!-- Logo -->
        <a href="${pageContext.request.contextPath}/home" class="brand-logo">
            <div class="logo-icon">🛍️</div>
            <div>Shop<span class="sphere">Sphere</span></div>
        </a>

        <!-- Search Form -->
        <form action="${pageContext.request.contextPath}/products" method="GET" class="search-form">
            <input type="text" name="q" placeholder="Search products, brands, gadgets, apparel..." 
                   value="<c:out value='${param.q}'/>" required autocomplete="off">
            <button type="submit" aria-label="Search">🔍</button>
        </form>

        <!-- Actions -->
        <div class="nav-actions">
            <a href="${pageContext.request.contextPath}/products" class="nav-link-item">
                <span>Catalog</span>
            </a>

            <!-- Wishlist Link -->
            <a href="${pageContext.request.contextPath}/wishlist" class="nav-link-item" title="Wishlist">
                <span>🤍</span>
                <span>Wishlist</span>
            </a>

            <!-- Cart Link -->
            <a href="${pageContext.request.contextPath}/cart" class="nav-link-item" title="Shopping Cart">
                <span>🛒</span>
                <span>Cart</span>
            </a>

            <!-- Authentication Links -->
            <c:choose>
                <c:when test="${sessionScope.currentUser != null}">
                    <a href="${pageContext.request.contextPath}/profile" class="nav-link-item" style="color: var(--primary); font-weight: 600;">
                        <span>👤</span>
                        <span><c:out value="${sessionScope.currentUser.name}"/></span>
                    </a>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-outline">Logout</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-sm btn-outline">Login</a>
                    <a href="${pageContext.request.contextPath}/register" class="btn btn-sm btn-primary">Register</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</header>

<!-- Category Horizontal Bar -->
<nav class="cat-nav">
    <div class="container cat-nav-inner">
        <a href="${pageContext.request.contextPath}/products" class="cat-nav-item ${empty param.category ? 'active' : ''}">
            ✨ All Products
        </a>
        <c:forEach var="cat" items="${applicationScope.globalCategories}">
            <a href="${pageContext.request.contextPath}/products?category=${cat.categoryId}" 
               class="cat-nav-item ${param.category == cat.categoryId ? 'active' : ''}">
                <c:out value="${cat.name}"/>
            </a>
        </c:forEach>
    </div>
</nav>

<!-- Global Flash Alerts -->
<div class="container" style="margin-top: 16px;">
    <c:if test="${not empty sessionScope.flashSuccess}">
        <div class="alert alert-success">
            <span>✓ <c:out value="${sessionScope.flashSuccess}"/></span>
            <button class="alert-close" aria-label="Close">&times;</button>
        </div>
        <c:remove var="flashSuccess" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.flashError}">
        <div class="alert alert-danger">
            <span>⚠️ <c:out value="${sessionScope.flashError}"/></span>
            <button class="alert-close" aria-label="Close">&times;</button>
        </div>
        <c:remove var="flashError" scope="session"/>
    </c:if>
</div>
