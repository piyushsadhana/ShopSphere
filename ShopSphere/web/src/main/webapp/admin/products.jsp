<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Product Inventory Management - ShopSphere Admin" />
<jsp:include page="../fragments/header.jsp" />

<div class="admin-layout">
    <c:set var="activeTab" value="products" />
    <jsp:include page="../fragments/sidebar.jsp" />

    <main class="admin-main">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 16px;">
            <div>
                <h1 style="font-size: 1.8rem; margin-bottom: 4px;">Inventory & Products</h1>
                <p style="color: var(--text-muted); font-size: 14px;">Create, update pricing, restock inventory, or manage catalog visibility.</p>
            </div>
        </div>

        <!-- Global Flash Alerts -->
        <c:if test="${not empty sessionScope.flashSuccess}">
            <div class="alert alert-success">
                <span>✓ <c:out value="${sessionScope.flashSuccess}"/></span>
                <button class="alert-close">&times;</button>
            </div>
            <c:remove var="flashSuccess" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.flashError}">
            <div class="alert alert-danger">
                <span>⚠️ <c:out value="${sessionScope.flashError}"/></span>
                <button class="alert-close">&times;</button>
            </div>
            <c:remove var="flashError" scope="session"/>
        </c:if>

        <!-- Add or Edit Product Form Card -->
        <div style="background: #fff; border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 28px; margin-bottom: 32px; box-shadow: var(--shadow-sm);">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                <h2 style="font-size: 1.25rem;">
                    <c:choose>
                        <c:when test="${not empty editProduct}">✏️ Edit Product: <c:out value="${editProduct.name}"/></c:when>
                        <c:otherwise>➕ Add New Product to Catalog</c:otherwise>
                    </c:choose>
                </h2>
                <c:if test="${not empty editProduct}">
                    <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-sm btn-secondary">Cancel Edit</a>
                </c:if>
            </div>

            <form action="${pageContext.request.contextPath}/admin/products" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                <input type="hidden" name="action" value="${not empty editProduct ? 'edit' : 'add'}">
                <c:if test="${not empty editProduct}">
                    <input type="hidden" name="productId" value="${editProduct.productId}">
                </c:if>

                <div class="form-row">
                    <div class="form-group" style="flex: 2;">
                        <label class="form-label" for="prodName">Product Title *</label>
                        <input type="text" id="prodName" name="name" class="form-control" 
                               value="<c:out value='${editProduct != null ? editProduct.name : ""}'/>" placeholder="e.g. Sony WH-1000XM5" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="prodBrand">Brand *</label>
                        <input type="text" id="prodBrand" name="brand" class="form-control" 
                               value="<c:out value='${editProduct != null ? editProduct.brand : ""}'/>" placeholder="e.g. Sony" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="prodCat">Category *</label>
                        <select id="prodCat" name="categoryId" class="form-control" required>
                            <c:forEach var="c" items="${categories}">
                                <option value="${c.categoryId}" ${(editProduct != null && editProduct.categoryId == c.categoryId) ? 'selected' : ''}>
                                    <c:out value="${c.name}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label" for="prodPrice">Base Price (₹) *</label>
                        <input type="number" step="0.01" id="prodPrice" name="price" class="form-control" 
                               value="${editProduct != null ? editProduct.price : ''}" placeholder="0.00" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="prodDiscount">Discount Percentage (%)</label>
                        <input type="number" step="0.01" id="prodDiscount" name="discount" class="form-control" 
                               value="${editProduct != null ? editProduct.discount : '0.00'}" placeholder="0.00">
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="prodStock">Stock Units *</label>
                        <input type="number" id="prodStock" name="stock" class="form-control" 
                               value="${editProduct != null ? editProduct.stock : '10'}" min="0" required>
                    </div>

                    <c:if test="${not empty editProduct}">
                        <div class="form-group">
                            <label class="form-label" for="prodStatus">Status</label>
                            <select id="prodStatus" name="status" class="form-control">
                                <option value="ACTIVE" ${editProduct.status == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                                <option value="INACTIVE" ${editProduct.status == 'INACTIVE' ? 'selected' : ''}>INACTIVE</option>
                                <option value="ARCHIVED" ${editProduct.status == 'ARCHIVED' ? 'selected' : ''}>ARCHIVED</option>
                            </select>
                        </div>
                    </c:if>
                </div>

                <div class="form-group">
                    <label class="form-label" for="prodDesc">Full Product Description</label>
                    <textarea id="prodDesc" name="description" rows="3" class="form-control" 
                              placeholder="Product features, specifications, and details..."><c:out value="${editProduct != null ? editProduct.description : ''}"/></textarea>
                </div>

                <div class="form-group">
                    <label class="form-label" for="prodImg">Product Image URL / Asset Path</label>
                    <input type="text" id="prodImg" name="imageUrl" class="form-control" 
                           value="<c:out value='${editProduct != null ? editProduct.imageUrl : "images/products/placeholder.svg"}'/>" 
                           placeholder="images/products/example.svg">
                </div>

                <button type="submit" class="btn btn-primary btn-lg">
                    <c:out value="${not empty editProduct ? 'Save Product Changes' : '+ Add Product'}"/>
                </button>
            </form>
        </div>

        <!-- Inventory List Table Card -->
        <div style="background: #fff; border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 24px; box-shadow: var(--shadow-sm);">
            <!-- Filter & Search Bar -->
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; flex-wrap: wrap; gap: 14px;">
                <h2 style="font-size: 1.25rem;">Catalog Products (${products.size()})</h2>

                <form action="${pageContext.request.contextPath}/admin/products" method="GET" style="display: flex; gap: 10px;">
                    <select name="category" class="form-control" style="width: auto;" onchange="this.form.submit()">
                        <option value="">All Categories</option>
                        <c:forEach var="c" items="${categories}">
                            <option value="${c.categoryId}" ${selectedCategory == c.categoryId ? 'selected' : ''}><c:out value="${c.name}"/></option>
                        </c:forEach>
                    </select>

                    <input type="text" name="q" placeholder="Search by name/brand..." class="form-control" 
                           value="<c:out value='${searchQuery}'/>" style="width: 220px;">
                    <button type="submit" class="btn btn-secondary">Filter</button>
                </form>
            </div>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Product</th>
                            <th>Category</th>
                            <th>Price</th>
                            <th>Discount</th>
                            <th>Stock</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="p" items="${products}">
                            <tr>
                                <td>
                                    <div style="display: flex; align-items: center; gap: 12px;">
                                        <img src="${pageContext.request.contextPath}/${p.imageUrl}" 
                                             alt="${p.name}" style="width: 40px; height: 40px; border-radius: 6px; object-fit: cover;">
                                        <div>
                                            <strong style="color: var(--text-main); font-size: 14px;"><c:out value="${p.name}"/></strong>
                                            <div style="font-size: 12px; color: var(--text-muted);"><c:out value="${p.brand}"/></div>
                                        </div>
                                    </div>
                                </td>
                                <td><c:out value="${p.categoryName}"/></td>
                                <td><strong>₹<fmt:formatNumber value="${p.price}" pattern="#,##0.00"/></strong></td>
                                <td>
                                    <c:if test="${p.discount > 0}">
                                        <span class="badge badge-danger"><fmt:formatNumber value="${p.discount}" maxFractionDigits="0"/>% OFF</span>
                                    </c:if>
                                </td>
                                <td>
                                    <span class="badge ${p.stock > 0 ? 'badge-success' : 'badge-danger'}">
                                        ${p.stock} units
                                    </span>
                                </td>
                                <td>
                                    <span class="badge ${p.status == 'ACTIVE' ? 'badge-success' : 'badge-warning'}">
                                        <c:out value="${p.status}"/>
                                    </span>
                                </td>
                                <td>
                                    <div style="display: flex; gap: 6px;">
                                        <a href="${pageContext.request.contextPath}/admin/products?editId=${p.productId}" class="btn btn-sm btn-outline">
                                            Edit
                                        </a>
                                        <form action="${pageContext.request.contextPath}/admin/products" method="POST" style="margin: 0;">
                                            <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="productId" value="${p.productId}">
                                            <button type="submit" class="btn btn-sm btn-secondary confirm-action" 
                                                    data-confirm="Delete or archive product '${p.name}'?" style="color: var(--danger);">
                                                Delete
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>

<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
