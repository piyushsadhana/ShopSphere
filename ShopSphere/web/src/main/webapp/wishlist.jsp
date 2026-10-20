<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="My Wishlist - ShopSphere" />
<jsp:include page="fragments/header.jsp" />
<jsp:include page="fragments/navbar.jsp" />

<main class="container" style="padding-top: 24px; padding-bottom: 48px;">
    <div class="section-title">
        <h2>My Wishlist (${wishlist.size()} items)</h2>
        <a href="${pageContext.request.contextPath}/products" style="font-size: 14px; font-weight: 600;">+ Explore More</a>
    </div>

    <c:choose>
        <c:when test="${empty wishlist}">
            <div style="text-align: center; padding: 64px 24px; background: #fff; border-radius: var(--radius-lg); border: 1px solid var(--border);">
                <div style="font-size: 56px; margin-bottom: 16px;">🤍</div>
                <h3 style="font-size: 1.5rem; margin-bottom: 8px;">Your wishlist is empty</h3>
                <p style="color: var(--text-muted); font-size: 14px; margin-bottom: 24px;">
                    Save products you love and want to purchase later.
                </p>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">
                    Explore Catalog
                </a>
            </div>
        </c:when>

        <c:otherwise>
            <div class="product-grid">
                <c:forEach var="item" items="${wishlist}">
                    <div class="product-card">
                        <div class="product-img-wrapper">
                            <c:if test="${item.product.discount > 0}">
                                <span class="product-badge-discount">
                                    <fmt:formatNumber value="${item.product.discount}" maxFractionDigits="0"/>% OFF
                                </span>
                            </c:if>

                            <!-- Remove from wishlist button -->
                            <form action="${pageContext.request.contextPath}/remove-from-wishlist" method="POST" style="margin: 0;">
                                <input type="hidden" name="productId" value="${item.productId}">
                                <button type="submit" class="product-wishlist-btn" title="Remove from Wishlist" style="color: var(--danger);">
                                    ✕
                                </button>
                            </form>

                            <a href="${pageContext.request.contextPath}/product-details?id=${item.productId}" style="display: contents;">
                                <img src="${pageContext.request.contextPath}/${item.product.imageUrl}" alt="${item.product.name}">
                            </a>
                        </div>

                        <div class="product-body">
                            <span class="product-brand"><c:out value="${item.product.brand}"/></span>
                            <h3 class="product-title">
                                <a href="${pageContext.request.contextPath}/product-details?id=${item.productId}">
                                    <c:out value="${item.product.name}"/>
                                </a>
                            </h3>

                            <div class="product-price-row">
                                <span class="current-price">₹<fmt:formatNumber value="${item.product.finalPrice}" pattern="#,##0.00"/></span>
                                <c:if test="${item.product.discount > 0}">
                                    <span class="original-price">₹<fmt:formatNumber value="${item.product.price}" pattern="#,##0.00"/></span>
                                </c:if>
                            </div>

                            <div class="product-actions">
                                <form action="${pageContext.request.contextPath}/add-to-cart" method="POST" style="flex: 1;">
                                    <input type="hidden" name="productId" value="${item.productId}">
                                    <input type="hidden" name="quantity" value="1">
                                    <button type="submit" class="btn btn-primary btn-block ajax-add-cart">
                                        🛒 Move to Cart
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<jsp:include page="fragments/footer.jsp" />
