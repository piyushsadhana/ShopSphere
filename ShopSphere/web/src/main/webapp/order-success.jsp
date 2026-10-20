<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Order Placed Successfully! - ShopSphere" />
<jsp:include page="fragments/header.jsp" />
<jsp:include page="fragments/navbar.jsp" />

<main class="container" style="padding-top: 48px; padding-bottom: 64px;">
    <div style="max-width: 600px; margin: 0 auto; background: #fff; border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 40px; text-align: center; box-shadow: var(--shadow-md);">
        
        <div style="width: 72px; height: 72px; background: var(--success-light); color: var(--success); border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 36px; margin: 0 auto 20px auto;">
            ✓
        </div>

        <h1 style="font-size: 1.8rem; margin-bottom: 8px;">Order Confirmed!</h1>
        <p style="color: var(--text-muted); font-size: 15px; margin-bottom: 28px;">
            Thank you for shopping with ShopSphere. We have received your order and our fulfillment team is preparing your package.
        </p>

        <c:if test="${not empty order}">
            <div style="background: var(--bg-subtle); border-radius: var(--radius-md); padding: 20px; text-align: left; margin-bottom: 28px; font-size: 14px;">
                <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
                    <span style="color: var(--text-muted);">Order Number:</span>
                    <strong style="color: var(--primary);"><c:out value="${order.orderNumber}"/></strong>
                </div>
                <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
                    <span style="color: var(--text-muted);">Order Date:</span>
                    <span><fmt:formatDate value="${order.createdAt}" pattern="dd MMM yyyy, hh:mm a"/></span>
                </div>
                <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
                    <span style="color: var(--text-muted);">Payment Method:</span>
                    <span><c:out value="${order.paymentMethod}"/></span>
                </div>
                <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
                    <span style="color: var(--text-muted);">Payment Status:</span>
                    <span class="badge ${order.paymentStatus == 'PAID' ? 'badge-success' : 'badge-warning'}">
                        <c:out value="${order.paymentStatus}"/>
                    </span>
                </div>
                <div style="border-top: 1px dashed var(--border); padding-top: 10px; margin-top: 10px; display: flex; justify-content: space-between;">
                    <span style="font-weight: 700;">Final Amount Paid/Due:</span>
                    <strong style="font-size: 16px; color: var(--text-main);">₹<fmt:formatNumber value="${order.finalAmount}" pattern="#,##0.00"/></strong>
                </div>
            </div>
        </c:if>

        <div style="display: flex; gap: 14px; justify-content: center; flex-wrap: wrap;">
            <c:if test="${not empty order}">
                <a href="${pageContext.request.contextPath}/order-details?id=${order.orderId}" class="btn btn-primary">
                    📦 Track Order Details
                </a>
            </c:if>
            <a href="${pageContext.request.contextPath}/orders" class="btn btn-secondary">
                View All Orders
            </a>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">
                Continue Shopping
            </a>
        </div>
    </div>
</main>

<jsp:include page="fragments/footer.jsp" />
