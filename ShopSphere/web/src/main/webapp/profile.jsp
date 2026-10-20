<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="My Profile - ShopSphere" />
<jsp:include page="fragments/header.jsp" />
<jsp:include page="fragments/navbar.jsp" />

<main class="container" style="padding-top: 24px; padding-bottom: 48px;">
    <!-- Profile Banner -->
    <div style="background: #fff; border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 28px; margin-bottom: 32px; display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 16px; box-shadow: var(--shadow-sm);">
        <div style="display: flex; align-items: center; gap: 18px;">
            <div style="width: 64px; height: 64px; border-radius: 50%; background: linear-gradient(135deg, var(--primary), var(--secondary)); color: #fff; font-size: 28px; font-weight: 700; display: flex; align-items: center; justify-content: center;">
                ${sessionScope.currentUser.name.substring(0, 1).toUpperCase()}
            </div>
            <div>
                <h1 style="font-size: 1.5rem; margin-bottom: 4px;"><c:out value="${sessionScope.currentUser.name}"/></h1>
                <p style="color: var(--text-muted); font-size: 14px;">
                    <c:out value="${sessionScope.currentUser.email}"/> • 
                    <span class="badge ${sessionScope.currentUser.admin ? 'badge-primary' : 'badge-success'}">
                        <c:out value="${sessionScope.currentUser.role}"/>
                    </span>
                </p>
            </div>
        </div>

        <div>
            <a href="${pageContext.request.contextPath}/orders" class="btn btn-secondary">
                📦 View My Orders (${orders.size()})
            </a>
        </div>
    </div>

    <!-- 2-Column Profile Layout -->
    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 32px;">
        
        <!-- Column 1: Personal Info & Change Password -->
        <div style="display: flex; flex-direction: column; gap: 28px;">
            <!-- Personal Info Card -->
            <div style="background: #fff; border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 28px; box-shadow: var(--shadow-sm);">
                <h2 style="font-size: 1.25rem; margin-bottom: 20px;">Personal Information</h2>

                <form action="${pageContext.request.contextPath}/profile" method="POST">
                    <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                    <input type="hidden" name="action" value="updateProfile">

                    <div class="form-group">
                        <label class="form-label" for="profName">Full Name</label>
                        <input type="text" id="profName" name="name" class="form-control" 
                               value="<c:out value='${sessionScope.currentUser.name}'/>" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="profEmail">Email Address (Read-only)</label>
                        <input type="email" id="profEmail" class="form-control" 
                               value="<c:out value='${sessionScope.currentUser.email}'/>" disabled>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="profPhone">Mobile Phone</label>
                        <input type="tel" id="profPhone" name="phone" class="form-control" 
                               value="<c:out value='${sessionScope.currentUser.phone}'/>" placeholder="+91 9876543210">
                    </div>

                    <button type="submit" class="btn btn-primary">Save Profile Changes</button>
                </form>
            </div>

            <!-- Password Change Card -->
            <div style="background: #fff; border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 28px; box-shadow: var(--shadow-sm);">
                <h2 style="font-size: 1.25rem; margin-bottom: 20px;">Change Password</h2>

                <form action="${pageContext.request.contextPath}/profile" method="POST">
                    <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                    <input type="hidden" name="action" value="changePassword">

                    <div class="form-group">
                        <label class="form-label" for="oldPass">Current Password</label>
                        <input type="password" id="oldPass" name="oldPassword" class="form-control" required>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label" for="newPass">New Password</label>
                            <input type="password" id="newPass" name="newPassword" class="form-control" minlength="6" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="confPass">Confirm New Password</label>
                            <input type="password" id="confPass" name="confirmPassword" class="form-control" minlength="6" required>
                        </div>
                    </div>

                    <button type="submit" class="btn btn-secondary">Update Password</button>
                </form>
            </div>
        </div>

        <!-- Column 2: Address Book -->
        <div style="display: flex; flex-direction: column; gap: 28px;">
            <div style="background: #fff; border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 28px; box-shadow: var(--shadow-sm);">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                    <h2 style="font-size: 1.25rem;">Saved Delivery Addresses</h2>
                </div>

                <c:choose>
                    <c:when test="${empty addresses}">
                        <p style="color: var(--text-muted); font-size: 14px; margin-bottom: 20px;">
                            You have no saved addresses yet. Add one below for faster checkout.
                        </p>
                    </c:when>
                    <c:otherwise>
                        <div style="display: flex; flex-direction: column; gap: 14px; margin-bottom: 24px;">
                            <c:forEach var="addr" items="${addresses}">
                                <div style="border: 1px solid var(--border); border-radius: var(--radius-md); padding: 16px; position: relative;">
                                    <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                                        <div>
                                            <strong style="font-size: 15px;"><c:out value="${addr.recipientName}"/></strong>
                                            <span style="font-size: 13px; color: var(--text-muted);">(${addr.phone})</span>
                                            <c:if test="${addr.default}">
                                                <span class="badge badge-primary" style="margin-left: 6px;">Default</span>
                                            </c:if>
                                        </div>

                                        <form action="${pageContext.request.contextPath}/profile" method="POST" style="margin: 0;">
                                            <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                                            <input type="hidden" name="action" value="deleteAddress">
                                            <input type="hidden" name="addressId" value="${addr.addressId}">
                                            <button type="submit" class="btn btn-sm btn-secondary confirm-action" 
                                                    data-confirm="Delete this address?" style="color: var(--danger); border: none;">
                                                🗑️
                                            </button>
                                        </form>
                                    </div>

                                    <p style="font-size: 13px; color: var(--text-muted); margin-top: 8px; line-height: 1.5;">
                                        <c:out value="${addr.addressLine1}"/>
                                        <c:if test="${not empty addr.addressLine2}">, <c:out value="${addr.addressLine2}"/></c:if><br>
                                        <c:out value="${addr.city}"/>, <c:out value="${addr.state}"/> - <c:out value="${addr.postalCode}"/>
                                    </p>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>

                <!-- Add New Address Form -->
                <div style="border-top: 1px dashed var(--border); padding-top: 20px;">
                    <h3 style="font-size: 15px; margin-bottom: 16px;">+ Add New Address</h3>
                    <form action="${pageContext.request.contextPath}/profile" method="POST">
                        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                        <input type="hidden" name="action" value="addAddress">

                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label" for="recName">Recipient Name *</label>
                                <input type="text" id="recName" name="recipientName" class="form-control" required>
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="recPhone">Phone Number *</label>
                                <input type="tel" id="recPhone" name="phone" class="form-control" required>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="recLine1">Street / Flat / House No. *</label>
                            <input type="text" id="recLine1" name="addressLine1" class="form-control" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="recLine2">Area / Landmark</label>
                            <input type="text" id="recLine2" name="addressLine2" class="form-control">
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label" for="recCity">City *</label>
                                <input type="text" id="recCity" name="city" class="form-control" required>
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="recState">State *</label>
                                <input type="text" id="recState" name="state" class="form-control" required>
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="recPin">PIN Code *</label>
                                <input type="text" id="recPin" name="postalCode" class="form-control" required>
                            </div>
                        </div>

                        <label style="display: flex; align-items: center; gap: 8px; font-size: 13px; margin-bottom: 16px; cursor: pointer;">
                            <input type="checkbox" name="isDefault" value="true">
                            <span>Set as default delivery address</span>
                        </label>

                        <button type="submit" class="btn btn-primary">Save New Address</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</main>

<jsp:include page="fragments/footer.jsp" />
