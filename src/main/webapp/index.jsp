<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>메인 페이지</title>
</head>
<body>
    <h2>Baseline 프로젝트 메인 페이지</h2>
    <hr>

    <%-- 세션에 loginId가 없으면 로그인 버튼 보이기 --%>
    <c:choose>
        <c:when test="${empty sessionScope.loginId}">
            <p>로그인이 필요합니다.</p>
            <button onclick="location.href='login.jsp'">로그인 하러가기</button>
        </c:when>
        
        <%-- 세션에 loginId가 있으면 환영 메시지와 로그아웃 버튼 보이기 --%>
        <c:otherwise>
            <p style="color: blue; font-weight: bold;">
                ${sessionScope.loginName}(${sessionScope.loginId})님, 환영합니다! 
                (권한: ${sessionScope.loginType == 'A' ? '관리자' : '일반유저'})
            </p>
            <button onclick="location.href='login?action=logout'">로그아웃</button>
        </c:otherwise>
    </c:choose>
</body>
</html>