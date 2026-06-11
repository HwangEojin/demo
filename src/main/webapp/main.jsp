<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>메인 페이지</title>
</head>
<body>
    <c:if test="${sessionScope.loginUser != null}">
        <h2>${sessionScope.loginUser.userNm}님, 환영합니다!</h2>
        <p>게시판 기능 등을 이곳에 구현할 수 있습니다.</p>
        <a href="logout.do">로그아웃</a>
    </c:if>
</body>
</html>