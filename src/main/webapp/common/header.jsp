<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${pageTitle} - BASELINE</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/notification.css">
    <script src="${pageContext.request.contextPath}/js/feedback.js?v=1.0" ></script>
    <script src="${pageContext.request.contextPath}/js/notification.js"></script>
</head>


 <!-- 알림 메시지 표시 영역 -->
    <c:if test="${not empty sessionScope.flash_notifications}">
        <div class="notification-container">
            <c:forEach items="${sessionScope.flash_notifications}" var="notification">
                <div class="notification notification-${notification.type}">
                    <span class="notification-text">${notification.message}</span>
                    <button class="close-btn">&times;</button>
                </div>
            </c:forEach>
        </div>
        <c:remove var="flash_notifications" scope="session" />
    </c:if>

<body>
    <header>
        <div class="container">
            <div class="logo">
                <a href="${pageContext.request.contextPath}/">BASELINE</a>
            </div>
            <nav>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/playground">PLAYGROUND</a></li>
                    <c:if test="${not empty sessionScope.loginUser}">
                        <c:if test="${sessionScope.loginUser.userTp == 'A'}">
                            <li><a href="${pageContext.request.contextPath}/admin/user_manage.do">사용자 관리</a></li>
                        </c:if>
                        <li><a href="${pageContext.request.contextPath}/user/mypage.do">마이페이지</a></li>
                        <li><a href="${pageContext.request.contextPath}/user/logout.do">로그아웃</a></li>
                    </c:if>
                    <c:if test="${empty sessionScope.loginUser}">
                        <li><a href="${pageContext.request.contextPath}/user/login.do">로그인</a></li>
                    </c:if>
                </ul>
            </nav>
        </div>
    </header>
    <main>
        <div class="container">
