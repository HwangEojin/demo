<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="pageTitle" value="Level 2 글쓰기" scope="request" />
<%@ include file="/common/header.jsp" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/playground.css">

<div class="board-container">
    <div class="board-header">
        <h2>글 작성</h2>
    </div>

    <form action="${pageContext.request.contextPath}/playground/level2/write.do" method="post" class="board-form" enctype="multipart/form-data">
        <div class="form-group">
            <label for="title">제목</label>
            <input type="text" id="title" name="title" class="form-control" required>
        </div>
        <div class="form-group">
            <label for="content">내용</label>
            <textarea id="content" name="content" class="form-control" rows="10" required></textarea>
        </div>
        <div class="form-group">
            <label for="file">첨부파일</label>
            <input type="file" id="file" name="file">
        </div>
        
        <div class="form-actions">
            <button type="submit" class="btn btn-primary">등록</button>
            <a href="${pageContext.request.contextPath}/playground/level2/list.do" class="btn">취소</a>
        </div>
    </form>
</div>

<%@ include file="/common/footer.jsp" %>
