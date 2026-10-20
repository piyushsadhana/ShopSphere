<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Coupon Management - ShopSphere Admin" />
<jsp:include page="../fragments/header.jsp" />

<div class="admin-layout">
    <c:set var="activeTab" value="coupons" />
    <jsp:include page="../fragments/sidebar.jsp" />

    <main class="admin-main">
        <div style="margin-bottom: 24px;">
            <h1 style="font-size: 1.8rem; margin-bottom: 4px;">Promotional Coupons & Discounts</h1>
            <p style="color: var(--text-muted); font-size: 14px;">Create targeted promotional codes, flash sale discounts, and minimum order requirements.</p>
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
            <!-- Create Coupon Card -->
            <div style="background: #fff; border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 24px; box-shadow: var(--shadow-sm); height: fit-content;">
                <h2 style="font-size: 1.2rem; margin-bottom: 18px;">➕ Create New Coupon</h2>

                <form action="${pageContext.request.contextPath}/admin/coupons" method="POST">
                    <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                    <input type="hidden" name="action" value="add">

                    <div class="form-group">
                        <label class="form-label" for="coupCode">Coupon Code *</label>
                        <input type="text" id="coupCode" name="code" class="form-control" 
                               placeholder="e.g. FLASH25" style="text-transform: uppercase;" required>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label" for="coupType">Type *</label>
                            <select id="coupType" name="discountType" class="form-control">
                                <option value="PERCENTAGE">Percentage (%)</option>
                                <option value="FIXED">Fixed Amount (₹)</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="coupVal">Discount Value *</label>
                            <input type="number" step="0.01" id="coupVal" name="discountValue" class="form-control" placeholder="10.00" required>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label" for="coupMin">Min. Order Value (₹)</label>
                            <input type="number" step="0.01" id="coupMin" name="minOrderValue" class="form-control" value="0.00">
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="coupLimit">Usage Limit</label>
                            <input type="number" id="coupLimit" name="usageLimit" class="form-control" value="100">
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="coupExp">Expiry Date *</label>
                        <input type="date" id="coupExp" name="expiryDate" class="form-control" value="2028-12-31" required>
                    </div>

                    <button type="submit" class="btn btn-primary btn-block">+ Create Coupon</button>
                </form>
            </div>

            <!-- Coupons Table Card -->
            <div style="background: #fff; border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 24px; box-shadow: var(--shadow-sm);">
                <h2 style="font-size: 1.2rem; margin-bottom: 18px;">Active Promotions (${coupons.size()})</h2>

                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Code</th>
                                <th>Discount</th>
                                <th>Min. Order</th>
                                <th>Redemptions</th>
                                <th>Expiry</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="cp" items="${coupons}">
                                <tr>
                                    <td>
                                        <code style="font-weight: 700; color: var(--primary); font-size: 14px;"><c:out value="${cp.code}"/></code>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${cp.discountType == 'PERCENTAGE'}">
                                                <strong><fmt:formatNumber value="${cp.discountValue}" maxFractionDigits="0"/>% OFF</strong>
                                            </c:when>
                                            <c:otherwise>
                                                <strong>₹<fmt:formatNumber value="${cp.discountValue}" pattern="#,##0"/> OFF</strong>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>₹<fmt:formatNumber value="${cp.minOrderValue}" pattern="#,##0.00"/></td>
                                    <td>${cp.usedCount} / ${cp.usageLimit}</td>
                                    <td><fmt:formatDate value="${cp.expiryDate}" pattern="dd MMM yyyy"/></td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/admin/coupons" method="POST" style="margin: 0;">
                                            <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="couponId" value="${cp.couponId}">
                                            <button type="submit" class="btn btn-sm btn-secondary confirm-action" 
                                                    data-confirm="Delete coupon '${cp.code}'?" style="color: var(--danger);">
                                                Delete
                                            </button>
                                        </form>
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
