<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="ShopSphere - Discover Quality Products, Best Prices" />
<jsp:include page="fragments/header.jsp" />
<jsp:include page="fragments/navbar.jsp" />

<main>
    <!-- Hero Banner -->
    <section class="hero-banner">
        <div class="container hero-content">
            <span class="hero-badge">✨ Super Summer Sale - Up to 50% Off</span>
            <h1 class="hero-title">Experience Smarter Online Shopping</h1>
            <p class="hero-subtitle">
                Explore thousands of handpicked electronics, designer fashion, home essentials, and bestselling books delivered fast right to your doorstep.
            </p>
            <div class="hero-buttons">
                <a href="${pageContext.request.contextPath}/products" class="btn btn-lg btn-primary">
                    🛍️ Start Shopping
                </a>
                <a href="${pageContext.request.contextPath}/products?sort=discount" class="btn btn-lg btn-secondary">
                    🔥 View Hot Deals
                </a>
            </div>
        </div>
    </section>

    <!-- Value Propositions -->
    <div class="container" style="margin-bottom: 48px;">
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 20px;">
            <div style="background: #fff; padding: 20px; border-radius: 12px; border: 1px solid var(--border); display: flex; align-items: center; gap: 14px;">
                <span style="font-size: 32px;">🚚</span>
                <div>
                    <h4 style="font-size: 15px; margin-bottom: 2px;">Free Delivery</h4>
                    <p style="font-size: 13px; color: var(--text-muted);">On all orders above ₹1,000</p>
                </div>
            </div>
            <div style="background: #fff; padding: 20px; border-radius: 12px; border: 1px solid var(--border); display: flex; align-items: center; gap: 14px;">
                <span style="font-size: 32px;">🛡️</span>
                <div>
                    <h4 style="font-size: 15px; margin-bottom: 2px;">100% Safe Payments</h4>
                    <p style="font-size: 13px; color: var(--text-muted);">Verified checkout protection</p>
                </div>
            </div>
            <div style="background: #fff; padding: 20px; border-radius: 12px; border: 1px solid var(--border); display: flex; align-items: center; gap: 14px;">
                <span style="font-size: 32px;">🔄</span>
                <div>
                    <h4 style="font-size: 15px; margin-bottom: 2px;">7-Day Easy Returns</h4>
                    <p style="font-size: 13px; color: var(--text-muted);">Hassle-free replacement policy</p>
                </div>
            </div>
            <div style="background: #fff; padding: 20px; border-radius: 12px; border: 1px solid var(--border); display: flex; align-items: center; gap: 14px;">
                <span style="font-size: 32px;">💬</span>
                <div>
                    <h4 style="font-size: 15px; margin-bottom: 2px;">24/7 Dedicated Support</h4>
                    <p style="font-size: 13px; color: var(--text-muted);">Friendly live assistance</p>
                </div>
            </div>
        </div>
    </div>

    <!-- Shop by Category Section -->
    <section class="container">
        <div class="section-title">
            <h2>Explore Categories</h2>
            <a href="${pageContext.request.contextPath}/products" style="font-weight: 600;">View All &rarr;</a>
        </div>
        <div class="category-grid">
            <c:forEach var="cat" items="${applicationScope.globalCategories}">
                <a href="${pageContext.request.contextPath}/products?category=${cat.categoryId}" class="category-card">
                    <img src="${pageContext.request.contextPath}/${cat.imageUrl}" alt="${cat.name}">
                    <h3><c:out value="${cat.name}"/></h3>
                    <span class="count"><c:out value="${cat.productCount}"/> Items Available</span>
                </a>
            </c:forEach>
        </div>
    </section>

    <!-- Featured Products Section -->
    <section class="container">
        <div class="section-title">
            <h2>Featured Products</h2>
            <a href="${pageContext.request.contextPath}/products" style="font-weight: 600;">Browse Catalog &rarr;</a>
        </div>

        <div class="product-grid">
            <c:forEach var="prod" items="${featuredProducts}">
                <div class="product-card">
                    <div class="product-img-wrapper">
                        <c:if test="${prod.discount > 0}">
                            <span class="product-badge-discount">
                                <fmt:formatNumber value="${prod.discount}" maxFractionDigits="0"/>% OFF
                            </span>
                        </c:if>

                        <!-- Wishlist Toggle -->
                        <form action="${pageContext.request.contextPath}/add-to-wishlist" method="POST" style="margin: 0;">
                            <input type="hidden" name="productId" value="${prod.productId}">
                            <button type="submit" class="product-wishlist-btn ajax-wishlist-btn" title="Add to Wishlist">
                                🤍
                            </button>
                        </form>

                        <a href="${pageContext.request.contextPath}/product-details?id=${prod.productId}" style="display: contents;">
                            <img src="${pageContext.request.contextPath}/${prod.imageUrl}" alt="${prod.name}">
                        </a>
                    </div>

                    <div class="product-body">
                        <span class="product-brand"><c:out value="${prod.brand}"/></span>
                        <h3 class="product-title">
                            <a href="${pageContext.request.contextPath}/product-details?id=${prod.productId}">
                                <c:out value="${prod.name}"/>
                            </a>
                        </h3>

                        <div class="product-rating">
                            <span>★</span>
                            <strong><fmt:formatNumber value="${prod.averageRating}" minFractionDigits="1" maxFractionDigits="1"/></strong>
                            <span class="review-count">(<c:out value="${prod.reviewCount}"/>)</span>
                        </div>

                        <div class="product-price-row">
                            <span class="current-price">₹<fmt:formatNumber value="${prod.finalPrice}" pattern="#,##0.00"/></span>
                            <c:if test="${prod.discount > 0}">
                                <span class="original-price">₹<fmt:formatNumber value="${prod.price}" pattern="#,##0.00"/></span>
                            </c:if>
                        </div>

                        <div class="product-actions">
                            <form action="${pageContext.request.contextPath}/add-to-cart" method="POST" style="flex: 1;">
                                <input type="hidden" name="productId" value="${prod.productId}">
                                <input type="hidden" name="quantity" value="1">
                                <button type="submit" class="btn btn-primary btn-block ajax-add-cart">
                                    🛒 Add to Cart
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </section>
</main>

<jsp:include page="fragments/footer.jsp" />
