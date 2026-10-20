<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="${product.name} - ShopSphere" />
<jsp:include page="fragments/header.jsp" />
<jsp:include page="fragments/navbar.jsp" />

<main class="container" style="padding-top: 24px; padding-bottom: 48px;">
    <!-- Breadcrumb -->
    <nav style="font-size: 13px; color: var(--text-muted); margin-bottom: 20px;">
        <a href="${pageContext.request.contextPath}/home">Home</a> &gt; 
        <a href="${pageContext.request.contextPath}/products?category=${product.categoryId}"><c:out value="${product.categoryName}"/></a> &gt; 
        <span><c:out value="${product.name}"/></span>
    </nav>

    <!-- Product Showcase Layout -->
    <div class="product-detail-layout">
        <!-- Gallery -->
        <div class="detail-gallery">
            <img src="${pageContext.request.contextPath}/${product.imageUrl}" alt="${product.name}">
        </div>

        <!-- Info & Buy Box -->
        <div class="detail-info">
            <span class="detail-brand"><c:out value="${product.brand}"/></span>
            <h1 class="detail-title"><c:out value="${product.name}"/></h1>

            <div class="product-rating" style="margin-bottom: 16px;">
                <span style="font-size: 18px;">★</span>
                <strong style="font-size: 16px;"><fmt:formatNumber value="${product.averageRating}" minFractionDigits="1" maxFractionDigits="1"/></strong>
                <span class="review-count">based on ${reviews.size()} verified customer reviews</span>
            </div>

            <!-- Price Breakdown Box -->
            <div class="detail-price-box">
                <div>
                    <span class="detail-current-price">₹<fmt:formatNumber value="${product.finalPrice}" pattern="#,##0.00"/></span>
                    <c:if test="${product.discount > 0}">
                        <div style="font-size: 14px; color: var(--text-muted); text-decoration: line-through;">
                            MRP: ₹<fmt:formatNumber value="${product.price}" pattern="#,##0.00"/>
                        </div>
                    </c:if>
                </div>
                <c:if test="${product.discount > 0}">
                    <span class="detail-savings">
                        Save ₹<fmt:formatNumber value="${product.savings}" pattern="#,##0.00"/> (<fmt:formatNumber value="${product.discount}" maxFractionDigits="0"/>% OFF)
                    </span>
                </c:if>
            </div>

            <!-- Stock Status -->
            <div class="stock-indicator">
                <c:choose>
                    <c:when test="${product.stock > 0}">
                        <span class="stock-in">● In Stock</span>
                        <span style="font-size: 13px; color: var(--text-muted);">(${product.stock} units remaining)</span>
                    </c:when>
                    <c:otherwise>
                        <span class="stock-out">● Currently Out of Stock</span>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Description -->
            <div style="margin-bottom: 24px; color: var(--text-muted); line-height: 1.7; font-size: 14px;">
                <p><c:out value="${product.description}"/></p>
            </div>

            <!-- Add to Cart Form -->
            <c:if test="${product.stock > 0}">
                <form action="${pageContext.request.contextPath}/add-to-cart" method="POST" style="margin-bottom: 24px;">
                    <input type="hidden" name="productId" value="${product.productId}">

                    <div style="display: flex; gap: 16px; align-items: center; margin-bottom: 20px;">
                        <label class="form-label" style="margin: 0;">Quantity:</label>
                        <div class="qty-control">
                            <button type="button" class="qty-btn minus">-</button>
                            <input type="number" name="quantity" class="qty-input" value="1" min="1" max="${product.stock}">
                            <button type="button" class="qty-btn plus">+</button>
                        </div>
                    </div>

                    <div style="display: flex; gap: 14px; flex-wrap: wrap;">
                        <button type="submit" class="btn btn-lg btn-primary ajax-add-cart" style="flex: 2; min-width: 200px;">
                            🛒 Add to Cart
                        </button>
                    </div>
                </form>
            </c:if>

            <!-- Wishlist Toggle -->
            <div style="display: flex; align-items: center; gap: 12px; margin-top: auto; padding-top: 16px; border-top: 1px solid var(--border);">
                <form action="${pageContext.request.contextPath}/add-to-wishlist" method="POST" style="margin: 0;">
                    <input type="hidden" name="productId" value="${product.productId}">
                    <input type="hidden" name="redirect" value="${pageContext.request.contextPath}/product-details?id=${product.productId}">
                    <button type="submit" class="btn btn-sm btn-secondary" style="display: flex; align-items: center; gap: 6px;">
                        <span>🤍</span>
                        <span>Save to Wishlist</span>
                    </button>
                </form>
            </div>
        </div>
    </div>

    <!-- Customer Reviews Section -->
    <section style="background: #fff; border-radius: var(--radius-lg); border: 1px solid var(--border); padding: 32px; margin-bottom: 40px; box-shadow: var(--shadow-sm);">
        <div class="section-title">
            <h2>Customer Reviews & Ratings</h2>
        </div>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 40px;">
            <!-- Review Submission Form -->
            <div>
                <h3 style="font-size: 1.15rem; margin-bottom: 16px;">Write a Review</h3>
                <c:choose>
                    <c:when test="${sessionScope.currentUser != null}">
                        <c:choose>
                            <c:when test="${alreadyReviewed}">
                                <div class="alert alert-success">
                                    <span>✓ You have already shared your feedback for this product. Thank you!</span>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <form action="${pageContext.request.contextPath}/add-review" method="POST">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                                    <input type="hidden" name="productId" value="${product.productId}">

                                    <div class="form-group">
                                        <label class="form-label" for="reviewRating">Rating</label>
                                        <select id="reviewRating" name="rating" class="form-control" style="max-width: 200px;" required>
                                            <option value="5">★★★★★ (5 Stars - Excellent)</option>
                                            <option value="4">★★★★☆ (4 Stars - Very Good)</option>
                                            <option value="3">★★★☆☆ (3 Stars - Average)</option>
                                            <option value="2">★★☆☆☆ (2 Stars - Below Average)</option>
                                            <option value="1">★☆☆☆☆ (1 Star - Poor)</option>
                                        </select>
                                    </div>

                                    <div class="form-group">
                                        <label class="form-label" for="reviewBody">Your Feedback</label>
                                        <textarea id="reviewBody" name="reviewText" rows="4" class="form-control" 
                                                  placeholder="What did you like or dislike about this product?" required></textarea>
                                    </div>

                                    <button type="submit" class="btn btn-primary">Submit Verified Review</button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        <div style="background: var(--bg-subtle); padding: 20px; border-radius: var(--radius-md); text-align: center;">
                            <p style="margin-bottom: 12px; font-size: 14px;">Only verified customers can leave a review.</p>
                            <a href="${pageContext.request.contextPath}/login" class="btn btn-sm btn-primary">Sign In to Review</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Existing Reviews List -->
            <div>
                <h3 style="font-size: 1.15rem; margin-bottom: 16px;">Recent Feedback (${reviews.size()})</h3>
                <c:choose>
                    <c:when test="${empty reviews}">
                        <p style="color: var(--text-muted); font-size: 14px;">No reviews yet. Be the first to review this product!</p>
                    </c:when>
                    <c:otherwise>
                        <div style="display: flex; flex-direction: column; gap: 16px; max-height: 400px; overflow-y: auto; padding-right: 8px;">
                            <c:forEach var="rev" items="${reviews}">
                                <div style="background: var(--bg-subtle); padding: 16px; border-radius: var(--radius-md);">
                                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px;">
                                        <strong style="font-size: 14px;"><c:out value="${rev.userName}"/></strong>
                                        <span style="color: #f59e0b; font-size: 14px;">
                                            <c:forEach begin="1" end="${rev.rating}">★</c:forEach><c:forEach begin="${rev.rating + 1}" end="5">☆</c:forEach>
                                        </span>
                                    </div>
                                    <p style="font-size: 13px; color: var(--text-main); line-height: 1.5;">
                                        <c:out value="${rev.reviewText}"/>
                                    </p>
                                    <span style="font-size: 11px; color: var(--text-muted); display: block; margin-top: 6px;">
                                        Verified Purchase • <fmt:formatDate value="${rev.createdAt}" pattern="dd MMM yyyy"/>
                                    </span>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </section>

    <!-- Related Products -->
    <c:if test="${not empty relatedProducts}">
        <section>
            <div class="section-title">
                <h2>Customers Also Viewed</h2>
            </div>
            <div class="product-grid">
                <c:forEach var="rProd" items="${relatedProducts}">
                    <div class="product-card">
                        <div class="product-img-wrapper">
                            <a href="${pageContext.request.contextPath}/product-details?id=${rProd.productId}" style="display: contents;">
                                <img src="${pageContext.request.contextPath}/${rProd.imageUrl}" alt="${rProd.name}">
                            </a>
                        </div>
                        <div class="product-body">
                            <span class="product-brand"><c:out value="${rProd.brand}"/></span>
                            <h3 class="product-title">
                                <a href="${pageContext.request.contextPath}/product-details?id=${rProd.productId}">
                                    <c:out value="${rProd.name}"/>
                                </a>
                            </h3>
                            <div class="product-price-row">
                                <span class="current-price">₹<fmt:formatNumber value="${rProd.finalPrice}" pattern="#,##0.00"/></span>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </section>
    </c:if>
</main>

<jsp:include page="fragments/footer.jsp" />
