<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="pageTitle" value="마이페이지" scope="request" />
<%@ include file="/common/header.jsp" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/mypage.css">

<div class="form-container mypage-form">
    <h2>마이페이지</h2>
    <p class="sub-heading">회원님의 정보를 수정할 수 있습니다.</p>
    
    <form action="${pageContext.request.contextPath}/user/update.do" method="post" data-encrypt="true" data-encrypt-mode="write">
        
        <c:if test="${not empty sessionScope.message}">
            <p class="success-message">${sessionScope.message}</p>
            <c:remove var="message" scope="session"/>
        </c:if>

        <div class="form-group">
            <label for="userId">아이디</label>
            <input type="text" id="userId" name="userId" value="${sessionScope.loginUser.userId}" readonly>
            <small>아이디는 변경할 수 없습니다.</small>
        </div>
        <div class="form-group">
            <label for="userPw">새 비밀번호</label>
            <input type="password" id="userPw" name="userPw" placeholder="변경할 경우에만 입력하세요">
        </div>
        <div class="form-group">
            <label for="userNm">이름</label>
            <input type="text" id="userNm" name="userNm" value="${sessionScope.loginUser.userNm}" required>
        </div>
        <div class="form-group">
            <label for="userEmail">이메일</label>
            <input type="email" id="userEmail" name="userEmail" value="${sessionScope.loginUser.userEmail}" required>
        </div>
        <div class="form-group">
            <label for="userNum">연락처</label>
            <input type="tel" id="userNum" name="userNum" value="${sessionScope.loginUser.userNum}">
        </div>
        <div class="form-actions">
            <button type="submit" class="btn">정보 수정</button>
        </div>
        <div class="withdrawal-section">
            <button type="button" id="withdrawalBtn" class="withdrawal-link">회원탈퇴</button>
        </div>
    </form>

<%@ include file="/user/passwordCheckModal.jsp" %>
<%@ include file="/user/withdrawalModal.jsp" %>
<%@ include file="/common/footer.jsp" %>