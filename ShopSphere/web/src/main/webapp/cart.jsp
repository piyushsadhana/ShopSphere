<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Shopping Cart - ShopSphere" />
<jsp:include page="fragments/header.jsp" />
<jsp:include page="fragments/navbar.jsp" />

<main class="container" style="padding-top: 24px; padding-bottom: 48px;">
    <h1 style="font-size: 1.8rem; margin-bottom: 24px;">Your Shopping Cart</h1>

    <c:choose>
        <c:when test="${empty cartItems}">
            <div style="text-align: center; padding: 64px 24px; background: #fff; border-radius: var(--radius-lg); border: 1px solid var(--border);">
                <div style="font-size: 56px; margin-bottom: 16px;">🛒</div>
                <h2 style="font-size: 1.5rem; margin-bottom: 8px;">Your shopping cart is empty</h2>
                <p style="color: var(--text-muted); font-size: 14px; margin-bottom: 24px;">
                    Looks like you haven't added anything to your cart yet. Explore our curated collections!
                </p>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-lg btn-primary">
                    Start Shopping Now
                </a>
            </div>
        </c:when>

        <c:otherwise>
            <div class="cart-layout">
                <!-- Items Table -->
                <div>
                    <table class="cart-table">
                        <thead>
                            <tr>
                                <th>Product</th>
                                <th>Price</th>
                                <th>Quantity</th>
                                <th>Subtotal</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${cartItems}">
                                <tr>
                                    <td>
                                        <div class="cart-prod-cell">
                                            <img src="${pageContext.request.contextPath}/${item.product.imageUrl}" 
                                                 alt="${item.product.name}" class="cart-prod-img">
                                            <div>
                                                <span style="font-size: 11px; text-transform: uppercase; font-weight: 700; color: var(--primary);">
                                                    <c:out value="${item.product.brand}"/>
                                                </span>
                                                <h4 style="font-size: 14px; margin-top: 2px;">
                                                    <a href="${pageContext.request.contextPath}/product-details?id=${item.productId}">
                                                        <c:out value="${item.product.name}"/>
                                                    </a>
                                                </h4>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <div style="font-weight: 600;">₹<fmt:formatNumber value="${item.product.finalPrice}" pattern="#,##0.00"/></div>
                                        <c:if test="${item.product.discount > 0}">
                                            <div style="font-size: 12px; color: var(--text-muted); text-decoration: line-through;">
                                                ₹<fmt:formatNumber value="${item.product.price}" pattern="#,##0.00"/>
                                            </div>
                                        </c:if>
                                    </td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/update-cart" method="POST" class="cart-qty-form" style="display: inline-block;">
                                            <input type="hidden" name="productId" value="${item.productId}">
                                            <div class="qty-control">
                                                <button type="button" class="qty-btn minus">-</button>
                                                <input type="number" name="quantity" class="qty-input" 
                                                       value="${item.quantity}" min="1" max="${item.product.stock}">
                                                <button type="button" class="qty-btn plus">+</button>
                                            </div>
                                        </form>
                                    </td>
                                    <td>
                                        <strong style="color: var(--text-main);">
                                            ₹<fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00"/>
                                        </strong>
                                    </td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/remove-from-cart" method="POST" style="margin: 0;">
                                            <input type="hidden" name="productId" value="${item.productId}">
                                            <button type="submit" class="btn btn-sm btn-secondary" title="Remove Item" style="color: var(--danger); border: none;">
                                                🗑️
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <div style="margin-top: 20px; display: flex; justify-content: space-between; align-items: center;">
                        <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">
                            &larr; Continue Shopping
                        </a>
                    </div>
                </div>

                <!-- Order Summary Sidebar -->
                <div>
                    <div class="summary-card">
                        <h3 style="font-size: 1.25rem; margin-bottom: 20px;">Order Summary</h3>

                        <!-- Coupon Box -->
                        <div style="margin-bottom: 20px; padding-bottom: 20px; border-bottom: 1px dashed var(--border);">
                            <c:choose>
                                <c:when test="${not empty appliedCoupon}">
                                    <div style="background: var(--success-light); border: 1px solid var(--success); border-radius: 8px; padding: 12px; display: flex; justify-content: space-between; align-items: center;">
                                        <div>
                                            <strong style="color: #065f46; font-size: 13px;">🎟️ Coupon Applied: <c:out value="${appliedCoupon.code}"/></strong>
                                            <div style="font-size: 12px; color: #047857;">You save ₹<fmt:formatNumber value="${discount}" pattern="#,##0.00"/></div>
                                        </div>
                                        <form action="${pageContext.request.contextPath}/cart" method="POST" style="margin: 0;">
                                            <input type="hidden" name="action" value="removeCoupon">
                                            <button type="submit" class="btn btn-sm btn-secondary" style="font-size: 11px; padding: 4px 8px;">Remove</button>
                                        </form>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <form action="${pageContext.request.contextPath}/cart" method="POST" style="display: flex; gap: 8px;">
                                        <input type="hidden" name="action" value="applyCoupon">
                                        <input type="text" name="couponCode" placeholder="Promo code (e.g. WELCOME10)" 
                                               class="form-control" style="text-transform: uppercase;" required>
                                        <button type="submit" class="btn btn-primary" style="white-space: nowrap;">Apply</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- Breakdown Rows -->
                        <div class="summary-row">
                            <span>Subtotal</span>
                            <span>₹<fmt:formatNumber value="${subtotal}" pattern="#,##0.00"/></span>
                        </div>

                        <c:if test="${discount > 0}">
                            <div class="summary-row" style="color: var(--success); font-weight: 600;">
                                <span>Coupon Discount</span>
                                <span>-₹<fmt:formatNumber value="${discount}" pattern="#,##0.00"/></span>
                            </div>
                        </c:if>

                        <div class="summary-row">
                            <span>Tax (18% GST)</span>
                            <span>₹<fmt:formatNumber value="${tax}" pattern="#,##0.00"/></span>
                        </div>

                        <div class="summary-row">
                            <span>Shipping</span>
                            <span>
                                <c:choose>
                                    <c:when test="${shipping == 0}">
                                        <strong style="color: var(--success);">FREE</strong>
                                    </c:when>
                                    <c:otherwise>
                                        ₹<fmt:formatNumber value="${shipping}" pattern="#,##0.00"/>
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>

                        <div class="summary-total">
                            <span>Total Amount</span>
                            <span>₹<fmt:formatNumber value="${grandTotal}" pattern="#,##0.00"/></span>
                        </div>

                        <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary btn-block btn-lg" style="margin-top: 24px;">
                            Proceed to Checkout &rarr;
                        </a>

                        <div style="text-align: center; margin-top: 16px; font-size: 12px; color: var(--text-muted);">
                            🔒 Safe & Encrypted 256-Bit Checkout
                        </div>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<jsp:include page="fragments/footer.jsp" />
