<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Secure Checkout - ShopSphere" />
<jsp:include page="fragments/header.jsp" />
<jsp:include page="fragments/navbar.jsp" />

<main class="container" style="padding-top: 24px; padding-bottom: 48px;">
    <h1 style="font-size: 1.8rem; margin-bottom: 24px;">Secure Checkout</h1>

    <form action="${pageContext.request.contextPath}/place-order" method="POST">
        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">

        <div class="cart-layout">
            <!-- Left Column: Address & Payment Selection -->
            <div>
                <!-- Step 1: Shipping Address -->
                <div style="background: #fff; border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 28px; margin-bottom: 28px; box-shadow: var(--shadow-sm);">
                    <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 20px;">
                        <span style="background: var(--primary); color: #fff; width: 28px; height: 28px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 14px;">1</span>
                        <h2 style="font-size: 1.25rem;">Delivery Address</h2>
                    </div>

                    <!-- Saved Addresses -->
                    <c:if test="${not empty addresses}">
                        <div style="display: flex; flex-direction: column; gap: 12px; margin-bottom: 20px;">
                            <c:forEach var="addr" items="${addresses}" varStatus="status">
                                <label style="display: flex; align-items: flex-start; gap: 12px; padding: 16px; border: 1px solid var(--border); border-radius: var(--radius-md); cursor: pointer; transition: var(--transition);">
                                    <input type="radio" name="addressId" value="${addr.addressId}" 
                                           ${(addr.default || status.first) ? 'checked' : ''} style="margin-top: 4px;">
                                    <div>
                                        <div style="font-weight: 700; font-size: 15px; margin-bottom: 4px;">
                                            <c:out value="${addr.recipientName}"/> 
                                            <span style="font-size: 13px; font-weight: normal; color: var(--text-muted);">(${addr.phone})</span>
                                            <c:if test="${addr.default}">
                                                <span class="badge badge-primary" style="font-size: 11px; margin-left: 6px;">Default</span>
                                            </c:if>
                                        </div>
                                        <p style="font-size: 14px; color: var(--text-muted); line-height: 1.5;">
                                            <c:out value="${addr.addressLine1}"/>
                                            <c:if test="${not empty addr.addressLine2}">, <c:out value="${addr.addressLine2}"/></c:if><br>
                                            <c:out value="${addr.city}"/>, <c:out value="${addr.state}"/> - <c:out value="${addr.postalCode}"/>, <c:out value="${addr.country}"/>
                                        </p>
                                    </div>
                                </label>
                            </c:forEach>
                        </div>
                    </c:if>

                    <!-- Toggle New Address -->
                    <div style="margin-top: 16px;">
                        <label style="display: flex; align-items: center; gap: 8px; font-weight: 600; cursor: pointer;">
                            <input type="radio" name="addressOption" value="new" ${empty addresses ? 'checked' : ''}>
                            <span>+ Enter a New Delivery Address</span>
                        </label>
                    </div>

                    <!-- New Address Form Fields -->
                    <div id="newAddressFields" style="margin-top: 20px; padding-top: 20px; border-top: 1px dashed var(--border); ${empty addresses ? '' : 'display: none;'}">
                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label" for="newRecipient">Recipient Name *</label>
                                <input type="text" id="newRecipient" name="newRecipientName" class="form-control" placeholder="Full name">
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="newPhone">Contact Phone *</label>
                                <input type="tel" id="newPhone" name="newPhone" class="form-control" placeholder="+91 9876543210">
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="newLine1">Address Line 1 (Flat, House no., Building) *</label>
                            <input type="text" id="newLine1" name="newAddressLine1" class="form-control" placeholder="Street address">
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="newLine2">Address Line 2 (Area, Landmark)</label>
                            <input type="text" id="newLine2" name="newAddressLine2" class="form-control" placeholder="Near landmark, sector...">
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label" for="newCity">City *</label>
                                <input type="text" id="newCity" name="newCity" class="form-control" placeholder="City">
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="newState">State *</label>
                                <input type="text" id="newState" name="newState" class="form-control" placeholder="State">
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="newPincode">Postal / PIN Code *</label>
                                <input type="text" id="newPincode" name="newPostalCode" class="form-control" placeholder="6-digit PIN">
                            </div>
                        </div>

                        <label style="display: flex; align-items: center; gap: 8px; font-size: 13px; cursor: pointer;">
                            <input type="checkbox" name="saveAddress" value="true" checked>
                            <span>Save this address to my profile address book</span>
                        </label>
                    </div>
                </div>

                <!-- Step 2: Payment Method Selection -->
                <div style="background: #fff; border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 28px; box-shadow: var(--shadow-sm);">
                    <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 20px;">
                        <span style="background: var(--primary); color: #fff; width: 28px; height: 28px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 14px;">2</span>
                        <h2 style="font-size: 1.25rem;">Payment Method</h2>
                    </div>

                    <div style="display: flex; flex-direction: column; gap: 14px; margin-bottom: 24px;">
                        <label style="display: flex; align-items: center; gap: 12px; padding: 14px 18px; border: 1px solid var(--border); border-radius: var(--radius-md); cursor: pointer;">
                            <input type="radio" name="paymentMethod" value="Cash on Delivery (COD)" checked>
                            <div>
                                <strong>💵 Cash on Delivery (COD)</strong>
                                <p style="font-size: 12px; color: var(--text-muted); margin: 0;">Pay with cash or UPI scanner upon delivery.</p>
                            </div>
                        </label>

                        <label style="display: flex; align-items: center; gap: 12px; padding: 14px 18px; border: 1px solid var(--border); border-radius: var(--radius-md); cursor: pointer;">
                            <input type="radio" name="paymentMethod" value="Credit / Debit Card (Demo)">
                            <div>
                                <strong>💳 Credit / Debit Card (Demo Sandbox)</strong>
                                <p style="font-size: 12px; color: var(--text-muted); margin: 0;">Instant simulated card confirmation (Visa/Mastercard/RuPay).</p>
                            </div>
                        </label>

                        <label style="display: flex; align-items: center; gap: 12px; padding: 14px 18px; border: 1px solid var(--border); border-radius: var(--radius-md); cursor: pointer;">
                            <input type="radio" name="paymentMethod" value="UPI / NetBanking (Demo)">
                            <div>
                                <strong>📱 UPI / NetBanking (Demo Sandbox)</strong>
                                <p style="font-size: 12px; color: var(--text-muted); margin: 0;">Simulated instant payment via Google Pay, PhonePe, Paytm.</p>
                            </div>
                        </label>
                    </div>

                    <!-- Delivery Instructions -->
                    <div class="form-group" style="margin: 0;">
                        <label class="form-label" for="orderNotes">Delivery Instructions (Optional)</label>
                        <textarea id="orderNotes" name="notes" rows="2" class="form-control" 
                                  placeholder="e.g. Leave package with security guard, gate code..."></textarea>
                    </div>
                </div>
            </div>

            <!-- Right Column: Final Order Summary -->
            <div>
                <div class="summary-card">
                    <h3 style="font-size: 1.25rem; margin-bottom: 16px;">Order Summary (${cartItems.size()} items)</h3>

                    <!-- Mini Cart Items Preview -->
                    <div style="max-height: 200px; overflow-y: auto; margin-bottom: 16px; border-bottom: 1px solid var(--border); padding-bottom: 12px;">
                        <c:forEach var="item" items="${cartItems}">
                            <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 10px;">
                                <img src="${pageContext.request.contextPath}/${item.product.imageUrl}" 
                                     alt="${item.product.name}" style="width: 40px; height: 40px; border-radius: 6px; object-fit: cover;">
                                <div style="flex: 1; font-size: 13px;">
                                    <div style="font-weight: 600; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 170px;">
                                        <c:out value="${item.product.name}"/>
                                    </div>
                                    <div style="color: var(--text-muted);">Qty: ${item.quantity} × ₹<fmt:formatNumber value="${item.product.finalPrice}" pattern="#,##0.00"/></div>
                                </div>
                                <div style="font-weight: 600; font-size: 13px;">
                                    ₹<fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00"/>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <!-- Price Calculations -->
                    <div class="summary-row">
                        <span>Items Subtotal</span>
                        <span>₹<fmt:formatNumber value="${subtotal}" pattern="#,##0.00"/></span>
                    </div>

                    <c:if test="${discount > 0}">
                        <div class="summary-row" style="color: var(--success); font-weight: 600;">
                            <span>Coupon Discount (${appliedCoupon.code})</span>
                            <span>-₹<fmt:formatNumber value="${discount}" pattern="#,##0.00"/></span>
                        </div>
                    </c:if>

                    <div class="summary-row">
                        <span>Tax (18% GST)</span>
                        <span>₹<fmt:formatNumber value="${tax}" pattern="#,##0.00"/></span>
                    </div>

                    <div class="summary-row">
                        <span>Shipping Fee</span>
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
                        <span>Order Total</span>
                        <span>₹<fmt:formatNumber value="${grandTotal}" pattern="#,##0.00"/></span>
                    </div>

                    <button type="submit" class="btn btn-primary btn-block btn-lg" style="margin-top: 24px;">
                        Place Order Now
                    </button>

                    <div style="text-align: center; margin-top: 14px; font-size: 12px; color: var(--text-muted);">
                        By placing order, you agree to ShopSphere Terms.
                    </div>
                </div>
            </div>
        </div>
    </form>
</main>

<jsp:include page="fragments/footer.jsp" />
