<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="pageTitle" value="로그인" scope="request" />
<%@ include file="/common/header.jsp" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">

<div class="form-container login-form">
    <h2>로그인</h2>
    <form action="${pageContext.request.contextPath}/user/login.do" method="post"  data-encrypt="true" data-encrypt-mode="submit">
        <c:if test="${not empty requestScope.message}">
            <p class="error-message">${requestScope.message}</p>
        </c:if>
        <div class="form-group">
            <label for="userId">아이디</label>
            <input type="text" id="userId" name="userId" required maxlength="15">
        </div>
        <div class="form-group">
            <label for="userPw">비밀번호</label>
            <input type="password" id="userPw" name="userPw" required maxlength="20">
        </div>
        <button type="submit" class="btn">로그인</button>
    </form>
    <div class="login-options">
        <span>계정이 없으신가요?</span>
        <a href="${pageContext.request.contextPath}/user/signup.do">회원가입</a>
    </div>
</div>

<%@ include file="/common/footer.jsp" %>