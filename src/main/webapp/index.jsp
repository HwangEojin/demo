<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Baseline</title>
</head>
<body>
    <h2>Baseline</h2>
    <hr>

    <%-- 세션에 loginId가 없으면 로그인 버튼 보이기 --%>
    <c:choose>
        <c:when test="${empty sessionScope.loginUser}">
            <p>로그인이 필요합니다.</p>
            <button onclick="location.href='login_form.do'">로그인</button>
        </c:when>
        
        <%-- 세션에 loginId가 있으면 환영 메시지와 로그아웃 버튼 보이기 --%>
        <c:otherwise>
            <p style="color: blue; font-weight: bold;">
                ${sessionScope.loginUser.userNm}(${sessionScope.loginUser.userId})님, 환영합니다! 
                (권한: ${sessionScope.loginUser.userTp == 'A' ? '관리자' : '일반유저'})
            </p>
            <button onclick="location.href='logout.do'">로그아웃</button>
        </c:otherwise>
    </c:choose>
</body>
</html>