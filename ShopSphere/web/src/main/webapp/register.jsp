<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Create an Account - ShopSphere" />
<jsp:include page="fragments/header.jsp" />
<jsp:include page="fragments/navbar.jsp" />

<main class="container">
    <div class="auth-card" style="max-width: 480px;">
        <div class="auth-header">
            <div class="logo-icon" style="margin: 0 auto 12px auto;">✨</div>
            <h1>Create Account</h1>
            <p style="color: var(--text-muted); font-size: 14px;">Join ShopSphere for exclusive offers and faster checkout</p>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">
                <span>⚠️ <c:out value="${errorMessage}"/></span>
                <button class="alert-close">&times;</button>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/register" method="POST">
            <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">

            <div class="form-group">
                <label class="form-label" for="regName">Full Name</label>
                <input type="text" id="regName" name="name" class="form-control" 
                       placeholder="e.g. Rahul Sharma" value="<c:out value='${paramName}'/>" required autofocus>
            </div>

            <div class="form-group">
                <label class="form-label" for="regEmail">Email Address</label>
                <input type="email" id="regEmail" name="email" class="form-control" 
                       placeholder="name@example.com" value="<c:out value='${paramEmail}'/>" required>
            </div>

            <div class="form-group">
                <label class="form-label" for="regPhone">Mobile Number (Optional)</label>
                <input type="tel" id="regPhone" name="phone" class="form-control" 
                       placeholder="+91 9876543210" value="<c:out value='${paramPhone}'/>">
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label" for="regPass">Password</label>
                    <input type="password" id="regPass" name="password" class="form-control" 
                           placeholder="At least 6 chars" minlength="6" required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="regConfirm">Confirm Password</label>
                    <input type="password" id="regConfirm" name="confirmPassword" class="form-control" 
                           placeholder="Repeat password" minlength="6" required>
                </div>
            </div>

            <button type="submit" class="btn btn-primary btn-block btn-lg" style="margin-top: 10px;">
                Create Account
            </button>
        </form>

        <div style="text-align: center; margin-top: 24px; font-size: 14px; color: var(--text-muted);">
            Already have an account? 
            <a href="${pageContext.request.contextPath}/login" style="font-weight: 600;">Sign In</a>
        </div>
    </div>
</main>

<jsp:include page="fragments/footer.jsp" />
