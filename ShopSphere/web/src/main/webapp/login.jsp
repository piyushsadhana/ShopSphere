<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Login - ShopSphere" />
<jsp:include page="fragments/header.jsp" />
<jsp:include page="fragments/navbar.jsp" />

<main class="container">
    <div class="auth-card">
        <div class="auth-header">
            <div class="logo-icon" style="margin: 0 auto 12px auto;">🔑</div>
            <h1>Welcome Back</h1>
            <p style="color: var(--text-muted); font-size: 14px;">Sign in to your ShopSphere account</p>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">
                <span>⚠️ <c:out value="${errorMessage}"/></span>
                <button class="alert-close">&times;</button>
            </div>
        </c:if>

        <c:if test="${not empty successMessage}">
            <div class="alert alert-success">
                <span>✓ <c:out value="${successMessage}"/></span>
                <button class="alert-close">&times;</button>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="POST">
            <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">

            <div class="form-group">
                <label class="form-label" for="loginEmail">Email Address</label>
                <input type="email" id="loginEmail" name="email" class="form-control" 
                       placeholder="name@example.com" value="<c:out value='${paramEmail}'/>" required autofocus>
            </div>

            <div class="form-group">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px;">
                    <label class="form-label" for="loginPass" style="margin: 0;">Password</label>
                    <a href="#" style="font-size: 12px;">Forgot password?</a>
                </div>
                <input type="password" id="loginPass" name="password" class="form-control" 
                       placeholder="••••••••" required>
            </div>

            <button type="submit" class="btn btn-primary btn-block btn-lg" style="margin-top: 12px;">
                Sign In
            </button>
        </form>

        <!-- Quick Demo Fill Boxes -->
        <div style="margin-top: 28px; padding-top: 20px; border-top: 1px dashed var(--border);">
            <p style="font-size: 12px; font-weight: 700; color: var(--text-muted); text-transform: uppercase; margin-bottom: 10px;">
                Quick Demo Credentials:
            </p>
            <div style="display: flex; gap: 8px;">
                <button type="button" class="btn btn-sm btn-secondary" style="flex: 1; font-size: 12px;"
                        onclick="document.getElementById('loginEmail').value='admin@shopsphere.com'; document.getElementById('loginPass').value='Admin@123';">
                    Fill Admin
                </button>
                <button type="button" class="btn btn-sm btn-secondary" style="flex: 1; font-size: 12px;"
                        onclick="document.getElementById('loginEmail').value='john@example.com'; document.getElementById('loginPass').value='Customer@123';">
                    Fill Customer
                </button>
            </div>
        </div>

        <div style="text-align: center; margin-top: 24px; font-size: 14px; color: var(--text-muted);">
            Don't have an account yet? 
            <a href="${pageContext.request.contextPath}/register" style="font-weight: 600;">Create Account</a>
        </div>
    </div>
</main>

<jsp:include page="fragments/footer.jsp" />
