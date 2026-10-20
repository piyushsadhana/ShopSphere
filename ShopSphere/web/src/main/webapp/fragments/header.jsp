<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle != null ? pageTitle : 'ShopSphere - Premier Online Shopping'}"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script>
        window.ShopSphereContext = "${pageContext.request.contextPath}";
    </script>
</head>
<body>
