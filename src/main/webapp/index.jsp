<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="pageTitle" value="메인 페이지" scope="request" />
<%@ include file="/common/header.jsp" %>

<div class="main-content">
    <c:choose>
        <c:when test="${empty sessionScope.loginUser}">
            <h1>BASELINE PLAYGROUND</h1>
            <p>취약점 진단 및 분석용 테스트 웹페이지</p>
            <a href="${pageContext.request.contextPath}/user/login.do" class="btn btn-main">로그인</a>
        </c:when>
        <c:otherwise>
            <h1>${sessionScope.loginUser.userNm} 계정으로 로그인</h1>
            <p>환영합니다.</p>
            <a href="${pageContext.request.contextPath}/playground" class="btn btn-main">PLAYGROUND</a>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="/common/footer.jsp" %>