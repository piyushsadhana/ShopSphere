<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Order Details ${order.orderNumber} - ShopSphere" />
<jsp:include page="fragments/header.jsp" />
<jsp:include page="fragments/navbar.jsp" />

<main class="container" style="padding-top: 24px; padding-bottom: 48px;">
    <!-- Breadcrumb -->
    <nav style="font-size: 13px; color: var(--text-muted); margin-bottom: 20px;">
        <a href="${pageContext.request.contextPath}/home">Home</a> &gt; 
        <a href="${pageContext.request.contextPath}/orders">My Orders</a> &gt; 
        <span><c:out value="${order.orderNumber}"/></span>
    </nav>

    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 16px;">
        <div>
            <h1 style="font-size: 1.8rem; margin-bottom: 4px;">Order <c:out value="${order.orderNumber}"/></h1>
            <p style="color: var(--text-muted); font-size: 14px;">
                Placed on <fmt:formatDate value="${order.createdAt}" pattern="dd MMMM yyyy 'at' hh:mm a"/>
            </p>
        </div>
        <div>
            <span class="badge ${order.orderStatus == 'DELIVERED' ? 'badge-success' : 'badge-primary'}" style="font-size: 14px; padding: 6px 14px;">
                Status: <c:out value="${order.orderStatus}"/>
            </span>
        </div>
    </div>

    <!-- Visual Order Tracking Bar -->
    <div style="background: #fff; border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 24px; margin-bottom: 32px; box-shadow: var(--shadow-sm);">
        <h3 style="font-size: 15px; margin-bottom: 20px; color: var(--text-muted);">Tracking Timeline</h3>
        <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; position: relative;">
            
            <div style="text-align: center;">
                <div style="width: 36px; height: 36px; border-radius: 50%; background: var(--primary); color: #fff; display: flex; align-items: center; justify-content: center; margin: 0 auto 8px auto; font-size: 16px;">
                    📝
                </div>
                <strong style="font-size: 13px; display: block;">Pending</strong>
                <span style="font-size: 11px; color: var(--text-muted);">Order Received</span>
            </div>

            <div style="text-align: center;">
                <div style="width: 36px; height: 36px; border-radius: 50%; background: ${(order.orderStatus == 'CONFIRMED' || order.orderStatus == 'SHIPPED' || order.orderStatus == 'DELIVERED') ? 'var(--primary)' : '#e2e8f0'}; color: ${(order.orderStatus == 'CONFIRMED' || order.orderStatus == 'SHIPPED' || order.orderStatus == 'DELIVERED') ? '#fff' : '#64748b'}; display: flex; align-items: center; justify-content: center; margin: 0 auto 8px auto; font-size: 16px;">
                    ✓
                </div>
                <strong style="font-size: 13px; display: block;">Confirmed</strong>
                <span style="font-size: 11px; color: var(--text-muted);">Items Prepared</span>
            </div>

            <div style="text-align: center;">
                <div style="width: 36px; height: 36px; border-radius: 50%; background: ${(order.orderStatus == 'SHIPPED' || order.orderStatus == 'DELIVERED') ? 'var(--primary)' : '#e2e8f0'}; color: ${(order.orderStatus == 'SHIPPED' || order.orderStatus == 'DELIVERED') ? '#fff' : '#64748b'}; display: flex; align-items: center; justify-content: center; margin: 0 auto 8px auto; font-size: 16px;">
                    🚚
                </div>
                <strong style="font-size: 13px; display: block;">Shipped</strong>
                <span style="font-size: 11px; color: var(--text-muted);">On the way</span>
            </div>

            <div style="text-align: center;">
                <div style="width: 36px; height: 36px; border-radius: 50%; background: ${order.orderStatus == 'DELIVERED' ? 'var(--success)' : '#e2e8f0'}; color: ${order.orderStatus == 'DELIVERED' ? '#fff' : '#64748b'}; display: flex; align-items: center; justify-content: center; margin: 0 auto 8px auto; font-size: 16px;">
                    🎉
                </div>
                <strong style="font-size: 13px; display: block;">Delivered</strong>
                <span style="font-size: 11px; color: var(--text-muted);">Package Handed Over</span>
            </div>
        </div>
    </div>

    <!-- 2-Column Content Layout -->
    <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 32px;">
        
        <!-- Left: Line Items & Shipping Details -->
        <div>
            <!-- Line Items Table -->
            <div style="background: #fff; border: 1px solid var(--border); border-radius: var(--radius-lg); overflow: hidden; margin-bottom: 24px; box-shadow: var(--shadow-sm);">
                <div style="padding: 16px 20px; background: var(--bg-subtle); border-bottom: 1px solid var(--border);">
                    <h3 style="font-size: 16px;">Itemized Products (${order.items.size()})</h3>
                </div>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Item</th>
                            <th>Unit Price</th>
                            <th>Qty</th>
                            <th style="text-align: right;">Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${order.items}">
                            <tr>
                                <td>
                                    <div style="display: flex; align-items: center; gap: 12px;">
                                        <img src="${pageContext.request.contextPath}/${item.productImageUrl}" 
                                             alt="${item.productName}" style="width: 44px; height: 44px; border-radius: 6px; object-fit: cover;">
                                        <div>
                                            <strong style="color: var(--text-main); font-size: 14px;"><c:out value="${item.productName}"/></strong>
                                        </div>
                                    </div>
                                </td>
                                <td>₹<fmt:formatNumber value="${item.productPrice}" pattern="#,##0.00"/></td>
                                <td>${item.quantity}</td>
                                <td style="text-align: right; font-weight: 700;">
                                    ₹<fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <!-- Delivery Address Snapshot -->
            <div style="background: #fff; border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 24px; box-shadow: var(--shadow-sm);">
                <h3 style="font-size: 16px; margin-bottom: 12px;">Shipping Address Snapshot</h3>
                <p style="font-size: 14px; line-height: 1.6; color: var(--text-muted); white-space: pre-line;">
                    <c:out value="${order.addressSnapshot}"/>
                </p>

                <c:if test="${not empty order.notes}">
                    <div style="margin-top: 16px; padding-top: 14px; border-top: 1px dashed var(--border); font-size: 13px;">
                        <span style="font-weight: 600;">Delivery Instructions:</span>
                        <p style="color: var(--text-muted); margin-top: 4px;"><c:out value="${order.notes}"/></p>
                    </div>
                </c:if>
            </div>
        </div>

        <!-- Right: Payment & Summary -->
        <div>
            <div class="summary-card" style="position: static;">
                <h3 style="font-size: 1.25rem; margin-bottom: 16px;">Billing Details</h3>

                <div class="summary-row">
                    <span>Subtotal</span>
                    <span>₹<fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00"/></span>
                </div>

                <c:if test="${order.discountAmount > 0}">
                    <div class="summary-row" style="color: var(--success); font-weight: 600;">
                        <span>Coupon Discount (${order.couponCode})</span>
                        <span>-₹<fmt:formatNumber value="${order.discountAmount}" pattern="#,##0.00"/></span>
                    </div>
                </c:if>

                <div class="summary-row">
                    <span>GST Tax</span>
                    <span>₹<fmt:formatNumber value="${order.taxAmount}" pattern="#,##0.00"/></span>
                </div>

                <div class="summary-row">
                    <span>Shipping Fee</span>
                    <span>
                        <c:choose>
                            <c:when test="${order.shippingFee == 0}">
                                <strong style="color: var(--success);">FREE</strong>
                            </c:when>
                            <c:otherwise>
                                ₹<fmt:formatNumber value="${order.shippingFee}" pattern="#,##0.00"/>
                            </c:otherwise>
                        </c:choose>
                    </span>
                </div>

                <div class="summary-total">
                    <span>Final Amount</span>
                    <span>₹<fmt:formatNumber value="${order.finalAmount}" pattern="#,##0.00"/></span>
                </div>

                <!-- Payment Record Info -->
                <div style="margin-top: 24px; padding-top: 16px; border-top: 1px solid var(--border); font-size: 13px;">
                    <h4 style="font-size: 14px; margin-bottom: 10px;">Payment Information</h4>
                    <div style="display: flex; justify-content: space-between; margin-bottom: 6px;">
                        <span style="color: var(--text-muted);">Method:</span>
                        <strong><c:out value="${order.paymentMethod}"/></strong>
                    </div>
                    <div style="display: flex; justify-content: space-between; margin-bottom: 6px;">
                        <span style="color: var(--text-muted);">Payment Status:</span>
                        <span class="badge ${order.paymentStatus == 'PAID' ? 'badge-success' : 'badge-warning'}">
                            <c:out value="${order.paymentStatus}"/>
                        </span>
                    </div>
                    <c:if test="${not empty payment}">
                        <div style="display: flex; justify-content: space-between; margin-bottom: 6px;">
                            <span style="color: var(--text-muted);">Ref:</span>
                            <code><c:out value="${payment.transactionRef}"/></code>
                        </div>
                    </c:if>
                </div>

                <a href="${pageContext.request.contextPath}/orders" class="btn btn-secondary btn-block" style="margin-top: 24px;">
                    &larr; Back to Order History
                </a>
            </div>
        </div>
    </div>
</main>

<jsp:include page="fragments/footer.jsp" />
