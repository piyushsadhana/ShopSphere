<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="My Orders - ShopSphere" />
<jsp:include page="fragments/header.jsp" />
<jsp:include page="fragments/navbar.jsp" />

<main class="container" style="padding-top: 24px; padding-bottom: 48px;">
    <div class="section-title">
        <h2>My Order History</h2>
        <a href="${pageContext.request.contextPath}/products" style="font-size: 14px; font-weight: 600;">+ Shop More</a>
    </div>

    <c:choose>
        <c:when test="${empty orders}">
            <div style="text-align: center; padding: 60px 24px; background: #fff; border-radius: var(--radius-lg); border: 1px solid var(--border);">
                <div style="font-size: 48px; margin-bottom: 16px;">📦</div>
                <h3 style="margin-bottom: 8px;">No orders found</h3>
                <p style="color: var(--text-muted); font-size: 14px; margin-bottom: 20px;">
                    You have not placed any orders yet. Start exploring our vast catalog!
                </p>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">
                    Start Shopping
                </a>
            </div>
        </c:when>

        <c:otherwise>
            <div style="display: flex; flex-direction: column; gap: 20px;">
                <c:forEach var="ord" items="${orders}">
                    <div style="background: #fff; border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 24px; box-shadow: var(--shadow-sm);">
                        
                        <!-- Order Card Header -->
                        <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border); padding-bottom: 16px; margin-bottom: 16px; flex-wrap: wrap; gap: 12px;">
                            <div>
                                <span style="font-size: 12px; color: var(--text-muted); text-transform: uppercase;">Order Number</span>
                                <div style="font-size: 16px; font-weight: 700; color: var(--primary);">
                                    <c:out value="${ord.orderNumber}"/>
                                </div>
                            </div>
                            <div>
                                <span style="font-size: 12px; color: var(--text-muted); text-transform: uppercase;">Placed On</span>
                                <div style="font-size: 14px; font-weight: 500;">
                                    <fmt:formatDate value="${ord.createdAt}" pattern="dd MMM yyyy, hh:mm a"/>
                                </div>
                            </div>
                            <div>
                                <span style="font-size: 12px; color: var(--text-muted); text-transform: uppercase;">Total</span>
                                <div style="font-size: 16px; font-weight: 700; color: var(--text-main);">
                                    ₹<fmt:formatNumber value="${ord.finalAmount}" pattern="#,##0.00"/>
                                </div>
                            </div>
                            <div>
                                <span style="font-size: 12px; color: var(--text-muted); text-transform: uppercase; display: block; margin-bottom: 4px;">Status</span>
                                <c:choose>
                                    <c:when test="${ord.orderStatus == 'DELIVERED'}"><span class="badge badge-success">Delivered</span></c:when>
                                    <c:when test="${ord.orderStatus == 'SHIPPED'}"><span class="badge badge-primary">Shipped</span></c:when>
                                    <c:when test="${ord.orderStatus == 'CONFIRMED'}"><span class="badge badge-primary">Confirmed</span></c:when>
                                    <c:when test="${ord.orderStatus == 'CANCELLED'}"><span class="badge badge-danger">Cancelled</span></c:when>
                                    <c:otherwise><span class="badge badge-warning">Processing</span></c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <!-- Items Preview List -->
                        <div style="display: flex; flex-direction: column; gap: 12px; margin-bottom: 18px;">
                            <c:forEach var="item" items="${ord.items}">
                                <div style="display: flex; align-items: center; justify-content: space-between; font-size: 14px;">
                                    <div style="display: flex; align-items: center; gap: 12px;">
                                        <img src="${pageContext.request.contextPath}/${item.productImageUrl}" 
                                             alt="${item.productName}" style="width: 44px; height: 44px; border-radius: 6px; object-fit: cover;">
                                        <div>
                                            <strong style="color: var(--text-main);"><c:out value="${item.productName}"/></strong>
                                            <div style="font-size: 12px; color: var(--text-muted);">
                                                Qty: ${item.quantity} × ₹<fmt:formatNumber value="${item.productPrice}" pattern="#,##0.00"/>
                                            </div>
                                        </div>
                                    </div>
                                    <div style="font-weight: 600;">
                                        ₹<fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00"/>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>

                        <!-- Footer Actions -->
                        <div style="border-top: 1px dashed var(--border); padding-top: 14px; display: flex; justify-content: space-between; align-items: center;">
                            <div style="font-size: 13px; color: var(--text-muted);">
                                Payment: <strong><c:out value="${ord.paymentMethod}"/></strong> 
                                (<span class="badge ${ord.paymentStatus == 'PAID' ? 'badge-success' : 'badge-warning'}"><c:out value="${ord.paymentStatus}"/></span>)
                            </div>
                            <a href="${pageContext.request.contextPath}/order-details?id=${ord.orderId}" class="btn btn-sm btn-outline">
                                View Details & Track &rarr;
                            </a>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<jsp:include page="fragments/footer.jsp" />
