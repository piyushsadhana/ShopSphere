<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Order Processing & Fulfillment - ShopSphere Admin" />
<jsp:include page="../fragments/header.jsp" />

<div class="admin-layout">
    <c:set var="activeTab" value="orders" />
    <jsp:include page="../fragments/sidebar.jsp" />

    <main class="admin-main">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 16px;">
            <div>
                <h1 style="font-size: 1.8rem; margin-bottom: 4px;">Orders & Fulfillment</h1>
                <p style="color: var(--text-muted); font-size: 14px;">Monitor incoming orders, print delivery manifests, and update fulfillment states.</p>
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

        <!-- View / Edit Specific Order Card -->
        <c:if test="${not empty viewOrder}">
            <div style="background: #fff; border: 2px solid var(--primary); border-radius: var(--radius-lg); padding: 28px; margin-bottom: 32px; box-shadow: var(--shadow-md);">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 1px solid var(--border); padding-bottom: 14px;">
                    <div>
                        <h2 style="font-size: 1.3rem;">Order Management: <c:out value="${viewOrder.orderNumber}"/></h2>
                        <span style="font-size: 13px; color: var(--text-muted);">Customer: <c:out value="${viewOrder.customerName}"/> (<c:out value="${viewOrder.customerEmail}"/>)</span>
                    </div>
                    <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-sm btn-secondary">Close Details</a>
                </div>

                <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 28px;">
                    <div>
                        <h3 style="font-size: 14px; margin-bottom: 10px; color: var(--text-muted);">Order Items</h3>
                        <table class="data-table" style="margin-bottom: 20px;">
                            <thead>
                                <tr>
                                    <th>Item</th>
                                    <th>Unit Price</th>
                                    <th>Qty</th>
                                    <th style="text-align: right;">Subtotal</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${viewOrder.items}">
                                    <tr>
                                        <td><strong><c:out value="${item.productName}"/></strong></td>
                                        <td>₹<fmt:formatNumber value="${item.productPrice}" pattern="#,##0.00"/></td>
                                        <td>${item.quantity}</td>
                                        <td style="text-align: right; font-weight: 700;">
                                            ₹<fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00"/>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>

                        <h3 style="font-size: 14px; margin-bottom: 6px; color: var(--text-muted);">Shipping Address</h3>
                        <p style="font-size: 13px; background: var(--bg-subtle); padding: 12px; border-radius: 6px; white-space: pre-line; line-height: 1.5;">
                            <c:out value="${viewOrder.addressSnapshot}"/>
                        </p>
                    </div>

                    <!-- Update Status Form -->
                    <div style="background: var(--bg-subtle); padding: 20px; border-radius: var(--radius-md);">
                        <h3 style="font-size: 15px; margin-bottom: 16px;">Update Order Lifecycle</h3>
                        <form action="${pageContext.request.contextPath}/admin/update-order" method="POST">
                            <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                            <input type="hidden" name="orderId" value="${viewOrder.orderId}">

                            <div class="form-group">
                                <label class="form-label" for="statSelect">Fulfillment Status</label>
                                <select id="statSelect" name="orderStatus" class="form-control">
                                    <option value="PENDING" ${viewOrder.orderStatus == 'PENDING' ? 'selected' : ''}>PENDING</option>
                                    <option value="CONFIRMED" ${viewOrder.orderStatus == 'CONFIRMED' ? 'selected' : ''}>CONFIRMED</option>
                                    <option value="SHIPPED" ${viewOrder.orderStatus == 'SHIPPED' ? 'selected' : ''}>SHIPPED</option>
                                    <option value="DELIVERED" ${viewOrder.orderStatus == 'DELIVERED' ? 'selected' : ''}>DELIVERED</option>
                                    <option value="CANCELLED" ${viewOrder.orderStatus == 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="paySelect">Payment Status</label>
                                <select id="paySelect" name="paymentStatus" class="form-control">
                                    <option value="PENDING" ${viewOrder.paymentStatus == 'PENDING' ? 'selected' : ''}>PENDING</option>
                                    <option value="PAID" ${viewOrder.paymentStatus == 'PAID' ? 'selected' : ''}>PAID</option>
                                    <option value="FAILED" ${viewOrder.paymentStatus == 'FAILED' ? 'selected' : ''}>FAILED</option>
                                    <option value="REFUNDED" ${viewOrder.paymentStatus == 'REFUNDED' ? 'selected' : ''}>REFUNDED</option>
                                </select>
                            </div>

                            <button type="submit" class="btn btn-primary btn-block">Save Status Update</button>
                        </form>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- Orders Table Card -->
        <div style="background: #fff; border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 24px; box-shadow: var(--shadow-sm);">
            
            <!-- Filters -->
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; flex-wrap: wrap; gap: 12px;">
                <h2 style="font-size: 1.2rem;">All Orders (${orders.size()})</h2>

                <div style="display: flex; gap: 8px;">
                    <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-sm ${empty selectedStatus ? 'btn-primary' : 'btn-secondary'}">All</a>
                    <a href="${pageContext.request.contextPath}/admin/orders?status=PENDING" class="btn btn-sm ${selectedStatus == 'PENDING' ? 'btn-primary' : 'btn-secondary'}">Pending</a>
                    <a href="${pageContext.request.contextPath}/admin/orders?status=CONFIRMED" class="btn btn-sm ${selectedStatus == 'CONFIRMED' ? 'btn-primary' : 'btn-secondary'}">Confirmed</a>
                    <a href="${pageContext.request.contextPath}/admin/orders?status=SHIPPED" class="btn btn-sm ${selectedStatus == 'SHIPPED' ? 'btn-primary' : 'btn-secondary'}">Shipped</a>
                    <a href="${pageContext.request.contextPath}/admin/orders?status=DELIVERED" class="btn btn-sm ${selectedStatus == 'DELIVERED' ? 'btn-primary' : 'btn-secondary'}">Delivered</a>
                </div>
            </div>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Order #</th>
                            <th>Customer</th>
                            <th>Date</th>
                            <th>Payment</th>
                            <th>Total</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="o" items="${orders}">
                            <tr>
                                <td>
                                    <strong style="color: var(--primary);"><c:out value="${o.orderNumber}"/></strong>
                                </td>
                                <td>
                                    <div><c:out value="${o.customerName}"/></div>
                                    <div style="font-size: 12px; color: var(--text-muted);"><c:out value="${o.customerEmail}"/></div>
                                </td>
                                <td><fmt:formatDate value="${o.createdAt}" pattern="dd MMM yyyy, hh:mm a"/></td>
                                <td>
                                    <span class="badge ${o.paymentStatus == 'PAID' ? 'badge-success' : 'badge-warning'}">
                                        <c:out value="${o.paymentStatus}"/>
                                    </span>
                                    <div style="font-size: 11px; color: var(--text-muted);"><c:out value="${o.paymentMethod}"/></div>
                                </td>
                                <td><strong>₹<fmt:formatNumber value="${o.finalAmount}" pattern="#,##0.00"/></strong></td>
                                <td>
                                    <span class="badge ${o.orderStatus == 'DELIVERED' ? 'badge-success' : (o.orderStatus == 'CANCELLED' ? 'badge-danger' : 'badge-primary')}">
                                        <c:out value="${o.orderStatus}"/>
                                    </span>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/orders?viewId=${o.orderId}" class="btn btn-sm btn-outline">
                                        View / Edit
                                    </a>
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
