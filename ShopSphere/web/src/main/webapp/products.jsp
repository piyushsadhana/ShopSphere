<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Product Catalog - ShopSphere" />
<jsp:include page="fragments/header.jsp" />
<jsp:include page="fragments/navbar.jsp" />

<main class="container" style="padding-top: 24px; padding-bottom: 48px;">
    <!-- Catalog Header -->
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 16px;">
        <div>
            <h2>Explore Products</h2>
            <p style="color: var(--text-muted); font-size: 14px;">
                Showing <strong>${products.size()}</strong> items
                <c:if test="${not empty searchQuery}">
                    for query "<strong><c:out value='${searchQuery}'/></strong>"
                </c:if>
            </p>
        </div>

        <!-- Sort Control -->
        <form action="${pageContext.request.contextPath}/products" method="GET" style="display: flex; align-items: center; gap: 10px;">
            <c:if test="${not empty selectedCategory}">
                <input type="hidden" name="category" value="${selectedCategory}">
            </c:if>
            <c:if test="${not empty searchQuery}">
                <input type="hidden" name="q" value="<c:out value='${searchQuery}'/>">
            </c:if>
            <c:if test="${not empty minPrice}">
                <input type="hidden" name="minPrice" value="${minPrice}">
            </c:if>
            <c:if test="${not empty maxPrice}">
                <input type="hidden" name="maxPrice" value="${maxPrice}">
            </c:if>

            <label for="sortSelect" style="font-size: 14px; font-weight: 600; color: var(--text-muted);">Sort By:</label>
            <select id="sortSelect" name="sort" class="form-control" style="width: auto; padding: 6px 12px;" onchange="this.form.submit()">
                <option value="newest" ${selectedSort == 'newest' ? 'selected' : ''}>Newest Arrivals</option>
                <option value="price_asc" ${selectedSort == 'price_asc' ? 'selected' : ''}>Price: Low to High</option>
                <option value="price_desc" ${selectedSort == 'price_desc' ? 'selected' : ''}>Price: High to Low</option>
                <option value="name_asc" ${selectedSort == 'name_asc' ? 'selected' : ''}>Name: A to Z</option>
                <option value="name_desc" ${selectedSort == 'name_desc' ? 'selected' : ''}>Name: Z to A</option>
                <option value="discount" ${selectedSort == 'discount' ? 'selected' : ''}>Biggest Discounts</option>
            </select>
        </form>
    </div>

    <!-- 2-Column Catalog Layout: Filter Sidebar + Products Grid -->
    <div style="display: grid; grid-template-columns: 260px 1fr; gap: 32px;">
        
        <!-- Filters Sidebar -->
        <aside style="background: #ffffff; border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 24px; height: fit-content; box-shadow: var(--shadow-sm);">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 1px solid var(--border); padding-bottom: 12px;">
                <h3 style="font-size: 16px;">Filter By</h3>
                <a href="${pageContext.request.contextPath}/products" style="font-size: 13px; color: var(--primary);">Reset All</a>
            </div>

            <form action="${pageContext.request.contextPath}/products" method="GET">
                <c:if test="${not empty searchQuery}">
                    <input type="hidden" name="q" value="<c:out value='${searchQuery}'/>">
                </c:if>
                <c:if test="${not empty selectedSort}">
                    <input type="hidden" name="sort" value="${selectedSort}">
                </c:if>

                <!-- Category Filter -->
                <div style="margin-bottom: 24px;">
                    <label class="form-label" style="margin-bottom: 12px;">Category</label>
                    <div style="display: flex; flex-direction: column; gap: 8px;">
                        <label style="font-size: 14px; cursor: pointer; display: flex; align-items: center; gap: 8px;">
                            <input type="radio" name="category" value="" ${empty selectedCategory ? 'checked' : ''} onchange="this.form.submit()">
                            <span>All Categories</span>
                        </label>
                        <c:forEach var="c" items="${categories}">
                            <label style="font-size: 14px; cursor: pointer; display: flex; align-items: center; gap: 8px;">
                                <input type="radio" name="category" value="${c.categoryId}" ${selectedCategory == c.categoryId ? 'checked' : ''} onchange="this.form.submit()">
                                <span><c:out value="${c.name}"/></span>
                            </label>
                        </c:forEach>
                    </div>
                </div>

                <!-- Price Range Filter -->
                <div style="margin-bottom: 24px;">
                    <label class="form-label" style="margin-bottom: 12px;">Price Range (₹)</label>
                    <div style="display: flex; gap: 8px; align-items: center; margin-bottom: 12px;">
                        <input type="number" name="minPrice" placeholder="Min" class="form-control" style="padding: 6px 10px;" value="${minPrice}">
                        <span>-</span>
                        <input type="number" name="maxPrice" placeholder="Max" class="form-control" style="padding: 6px 10px;" value="${maxPrice}">
                    </div>
                    <button type="submit" class="btn btn-sm btn-outline btn-block">Apply Price Filter</button>
                </div>
            </form>
        </aside>

        <!-- Product Listing Grid -->
        <section>
            <c:choose>
                <c:when test="${empty products}">
                    <div style="text-align: center; padding: 60px 20px; background: #fff; border-radius: var(--radius-lg); border: 1px solid var(--border);">
                        <div style="font-size: 48px; margin-bottom: 16px;">🔍</div>
                        <h3 style="margin-bottom: 8px;">No matching products found</h3>
                        <p style="color: var(--text-muted); font-size: 14px; margin-bottom: 20px;">
                            Try loosening your search terms or clearing price filters.
                        </p>
                        <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">
                            Browse All Products
                        </a>
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="product-grid">
                        <c:forEach var="prod" items="${products}">
                            <div class="product-card">
                                <div class="product-img-wrapper">
                                    <c:if test="${prod.discount > 0}">
                                        <span class="product-badge-discount">
                                            <fmt:formatNumber value="${prod.discount}" maxFractionDigits="0"/>% OFF
                                        </span>
                                    </c:if>

                                    <!-- Wishlist Button -->
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
                </c:otherwise>
            </c:choose>
        </section>
    </div>
</main>

<jsp:include page="fragments/footer.jsp" />
