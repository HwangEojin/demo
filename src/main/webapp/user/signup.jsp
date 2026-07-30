<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="pageTitle" value="회원가입" scope="request" />
<%@ include file="/common/header.jsp" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/signup.css">

<div class="form-container signup-form">
    <h2>회원가입</h2>
    <form action="${pageContext.request.contextPath}/user/signup.do" method="post" data-encrypt="true" data-encrypt-mode="write">
        
        <c:if test="${not empty requestScope.message}">
            <p class="error-message">${requestScope.message}</p>
        </c:if>
        <div class="form-group">
            <label for="userId">아이디</label>
            <input type="text" id="userId" name="userId" required>
        </div>
        <div class="form-group">
            <label for="userPw">비밀번호</label>
            <input type="password" id="userPw" name="userPw" required pattern="^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$" title="비밀번호는 8자리 이상이며, 영문, 숫자, 특수문자를 모두 포함해야 합니다.">
        </div>
        <div class="form-group">
            <label for="userNm">이름</label>
            <input type="text" id="userNm" name="userNm" required>
        </div>
        <div class="form-group">
            <label for="userEmail">이메일</label>
            <input type="email" id="userEmail" name="userEmail" required>
        </div>
        <div class="form-group">
            <label for="userNum">연락처</label>
            <input type="tel" id="userNum" name="userNum" placeholder="010-0000-0000">
        </div>
        <button type="submit" class="btn">가입하기</button>
    </form>
</div>

<%@ include file="/common/footer.jsp" %>