<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Admin Dashboard - ShopSphere Control Center" />
<jsp:include page="../fragments/header.jsp" />

<div class="admin-layout">
    <!-- Sidebar -->
    <c:set var="activeTab" value="dashboard" />
    <jsp:include page="../fragments/sidebar.jsp" />

    <!-- Main Content -->
    <main class="admin-main">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 28px;">
            <div>
                <h1 style="font-size: 1.8rem; margin-bottom: 4px;">Executive Dashboard</h1>
                <p style="color: var(--text-muted); font-size: 14px;">Real-time overview of ShopSphere sales, products, and platform metrics.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-primary">+ Manage Inventory</a>
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

        <!-- Stat KPI Cards Grid -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon" style="background: #eff6ff; color: #2563eb;">📦</div>
                <div class="stat-details">
                    <h3>${productCount}</h3>
                    <p>Active Products</p>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon" style="background: #fdf2f8; color: #db2777;">🗂️</div>
                <div class="stat-details">
                    <h3>${categoryCount}</h3>
                    <p>Live Categories</p>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon" style="background: #f0fdf4; color: #16a34a;">👥</div>
                <div class="stat-details">
                    <h3>${customerCount}</h3>
                    <p>Registered Customers</p>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon" style="background: #fefce8; color: #ca8a04;">📑</div>
                <div class="stat-details">
                    <h3>${orderCount}</h3>
                    <p>Total Orders</p>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon" style="background: #f5f3ff; color: #7c3aed;">💰</div>
                <div class="stat-details">
                    <h3>₹<fmt:formatNumber value="${totalRevenue}" pattern="#,##0"/></h3>
                    <p>Total Sales Revenue</p>
                </div>
            </div>
        </div>

        <!-- 2-Column Tables: Recent Orders & Recent Users -->
        <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 28px;">
            
            <!-- Recent Orders -->
            <div style="background: #fff; border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 24px; box-shadow: var(--shadow-sm);">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px;">
                    <h2 style="font-size: 1.15rem;">Recent Orders</h2>
                    <a href="${pageContext.request.contextPath}/admin/orders" style="font-size: 13px; font-weight: 600;">View All Orders &rarr;</a>
                </div>

                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Order #</th>
                                <th>Customer</th>
                                <th>Total</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="ord" items="${recentOrders}">
                                <tr>
                                    <td>
                                        <strong style="color: var(--primary);"><c:out value="${ord.orderNumber}"/></strong>
                                    </td>
                                    <td><c:out value="${ord.customerName}"/></td>
                                    <td><strong>₹<fmt:formatNumber value="${ord.finalAmount}" pattern="#,##0.00"/></strong></td>
                                    <td>
                                        <span class="badge ${ord.orderStatus == 'DELIVERED' ? 'badge-success' : 'badge-warning'}">
                                            <c:out value="${ord.orderStatus}"/>
                                        </span>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin/orders?viewId=${ord.orderId}" class="btn btn-sm btn-outline">
                                            View
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Recent Users -->
            <div style="background: #fff; border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 24px; box-shadow: var(--shadow-sm);">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px;">
                    <h2 style="font-size: 1.15rem;">Recent Users</h2>
                    <a href="${pageContext.request.contextPath}/admin/users" style="font-size: 13px; font-weight: 600;">View All &rarr;</a>
                </div>

                <div style="display: flex; flex-direction: column; gap: 14px;">
                    <c:forEach var="u" items="${recentUsers}">
                        <div style="display: flex; align-items: center; justify-content: space-between; font-size: 13px;">
                            <div>
                                <strong style="display: block; color: var(--text-main);"><c:out value="${u.name}"/></strong>
                                <span style="color: var(--text-muted);"><c:out value="${u.email}"/></span>
                            </div>
                            <span class="badge ${u.admin ? 'badge-primary' : 'badge-success'}">
                                <c:out value="${u.role}"/>
                            </span>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </main>
</div>

<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
