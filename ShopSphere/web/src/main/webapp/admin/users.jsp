<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="User Management - ShopSphere Admin" />
<jsp:include page="../fragments/header.jsp" />

<div class="admin-layout">
    <c:set var="activeTab" value="users" />
    <jsp:include page="../fragments/sidebar.jsp" />

    <main class="admin-main">
        <div style="margin-bottom: 24px;">
            <h1 style="font-size: 1.8rem; margin-bottom: 4px;">Registered Users & Customers</h1>
            <p style="color: var(--text-muted); font-size: 14px;">Oversee registered shoppers, administrator credentials, and account statuses.</p>
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

        <div style="background: #fff; border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 24px; box-shadow: var(--shadow-sm);">
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>User ID</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Role</th>
                            <th>Status</th>
                            <th>Registered</th>
                            <th>Update Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="u" items="${users}">
                            <tr>
                                <td>#${u.userId}</td>
                                <td><strong><c:out value="${u.name}"/></strong></td>
                                <td><c:out value="${u.email}"/></td>
                                <td><c:out value="${not empty u.phone ? u.phone : 'N/A'}"/></td>
                                <td>
                                    <span class="badge ${u.admin ? 'badge-primary' : 'badge-success'}">
                                        <c:out value="${u.role}"/>
                                    </span>
                                </td>
                                <td>
                                    <span class="badge ${u.status == 'ACTIVE' ? 'badge-success' : 'badge-danger'}">
                                        <c:out value="${u.status}"/>
                                    </span>
                                </td>
                                <td><fmt:formatDate value="${u.createdAt}" pattern="dd MMM yyyy"/></td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/admin/update-user-status" method="POST" style="display: flex; gap: 6px;">
                                        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                                        <input type="hidden" name="userId" value="${u.userId}">
                                        
                                        <select name="status" class="form-control" style="width: auto; padding: 4px 8px; font-size: 13px;">
                                            <option value="ACTIVE" ${u.status == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                                            <option value="INACTIVE" ${u.status == 'INACTIVE' ? 'selected' : ''}>INACTIVE</option>
                                            <option value="SUSPENDED" ${u.status == 'SUSPENDED' ? 'selected' : ''}>SUSPENDED</option>
                                        </select>
                                        <button type="submit" class="btn btn-sm btn-secondary">Apply</button>
                                    </form>
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
