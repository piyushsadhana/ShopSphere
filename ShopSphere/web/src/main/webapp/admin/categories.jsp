<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Category Management - ShopSphere Admin" />
<jsp:include page="../fragments/header.jsp" />

<div class="admin-layout">
    <c:set var="activeTab" value="categories" />
    <jsp:include page="../fragments/sidebar.jsp" />

    <main class="admin-main">
        <div style="margin-bottom: 24px;">
            <h1 style="font-size: 1.8rem; margin-bottom: 4px;">Category Taxonomy</h1>
            <p style="color: var(--text-muted); font-size: 14px;">Organize your product hierarchy and storefront department tiles.</p>
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

        <div style="display: grid; grid-template-columns: 1fr 2fr; gap: 32px;">
            <!-- Category Form Card -->
            <div style="background: #fff; border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 24px; box-shadow: var(--shadow-sm); height: fit-content;">
                <h2 style="font-size: 1.2rem; margin-bottom: 18px;">
                    <c:choose>
                        <c:when test="${not empty editCategory}">✏️ Edit Category</c:when>
                        <c:otherwise>➕ Add New Category</c:otherwise>
                    </c:choose>
                </h2>

                <form action="${pageContext.request.contextPath}/admin/categories" method="POST">
                    <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                    <input type="hidden" name="action" value="${not empty editCategory ? 'edit' : 'add'}">
                    <c:if test="${not empty editCategory}">
                        <input type="hidden" name="categoryId" value="${editCategory.categoryId}">
                    </c:if>

                    <div class="form-group">
                        <label class="form-label" for="catName">Category Title *</label>
                        <input type="text" id="catName" name="name" class="form-control" 
                               value="<c:out value='${editCategory != null ? editCategory.name : ""}'/>" placeholder="e.g. Gaming & VR" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="catDesc">Description</label>
                        <textarea id="catDesc" name="description" rows="3" class="form-control" 
                                  placeholder="Brief summary of items in this category..."><c:out value="${editCategory != null ? editCategory.description : ''}"/></textarea>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="catImg">Banner / Icon Asset Path</label>
                        <input type="text" id="catImg" name="imageUrl" class="form-control" 
                               value="<c:out value='${editCategory != null ? editCategory.imageUrl : "images/cat-default.svg"}'/>" 
                               placeholder="images/cat-electronics.svg">
                    </div>

                    <c:if test="${not empty editCategory}">
                        <div class="form-group">
                            <label class="form-label" for="catStatus">Status</label>
                            <select id="catStatus" name="status" class="form-control">
                                <option value="ACTIVE" ${editCategory.status == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                                <option value="INACTIVE" ${editCategory.status == 'INACTIVE' ? 'selected' : ''}>INACTIVE</option>
                            </select>
                        </div>
                    </c:if>

                    <div style="display: flex; gap: 10px;">
                        <button type="submit" class="btn btn-primary btn-block">
                            <c:out value="${not empty editCategory ? 'Update Category' : '+ Create Category'}"/>
                        </button>
                        <c:if test="${not empty editCategory}">
                            <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-secondary">Cancel</a>
                        </c:if>
                    </div>
                </form>
            </div>

            <!-- Categories Table Card -->
            <div style="background: #fff; border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 24px; box-shadow: var(--shadow-sm);">
                <h2 style="font-size: 1.2rem; margin-bottom: 18px;">Active Categories (${categories.size()})</h2>

                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Category</th>
                                <th>Products</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${categories}">
                                <tr>
                                    <td>
                                        <div style="display: flex; align-items: center; gap: 12px;">
                                            <img src="${pageContext.request.contextPath}/${c.imageUrl}" 
                                                 alt="${c.name}" style="width: 36px; height: 36px; border-radius: 6px; object-fit: contain;">
                                            <div>
                                                <strong style="color: var(--text-main); font-size: 14px;"><c:out value="${c.name}"/></strong>
                                                <div style="font-size: 12px; color: var(--text-muted);"><c:out value="${c.description}"/></div>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <span class="badge badge-primary">${c.productCount} items</span>
                                    </td>
                                    <td>
                                        <span class="badge ${c.status == 'ACTIVE' ? 'badge-success' : 'badge-danger'}">
                                            <c:out value="${c.status}"/>
                                        </span>
                                    </td>
                                    <td>
                                        <div style="display: flex; gap: 6px;">
                                            <a href="${pageContext.request.contextPath}/admin/categories?editId=${c.categoryId}" class="btn btn-sm btn-outline">
                                                Edit
                                            </a>
                                            <form action="${pageContext.request.contextPath}/admin/categories" method="POST" style="margin: 0;">
                                                <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="categoryId" value="${c.categoryId}">
                                                <button type="submit" class="btn btn-sm btn-secondary confirm-action" 
                                                        data-confirm="Remove or deactivate category '${c.name}'?" style="color: var(--danger);">
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
        </div>
    </main>
</div>

<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
