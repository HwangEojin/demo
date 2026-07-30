<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Level 1 게시글 보기" scope="request" />
<%@ include file="/common/header.jsp" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/playground.css">

<div class="board-container">
    <div class="board-view">
        <div class="view-header">
            <h3 class="view-title">${board.boardTitle}</h3>
            <div class="view-meta">
                <span class="author">작성자: ${board.boardAuthor}</span>
                <span class="date">작성일: <fmt:formatDate value="${board.uploadTime}" pattern="yyyy-MM-dd HH:mm"/></span>
            </div>
        </div>
        <div class="view-content">
          <p style="white-space: pre-wrap;">${board.boardContents}</p>
        </div>
    </div>

    <div class="view-attachment">
        <h4>첨부파일</h4>
        <c:choose>
            <c:when test="${not empty fileList}">
                <ul>
                    <c:forEach items="${fileList}" var="file">
                        <li><a href="${pageContext.request.contextPath}/playground/level1/download.do?fileName=${file.savedFileName}">${file.originalFileName}</a></li>
                    </c:forEach>
                </ul>
            </c:when>
            <c:otherwise>
                <p>첨부된 파일이 없습니다.</p>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="page-controls">
        <a href="${pageContext.request.contextPath}/playground/level1/list.do" class="btn">목록</a>
        <c:if test="${not empty sessionScope.loginUser && sessionScope.loginUser.userNm == board.boardAuthor}">
            <a href="${pageContext.request.contextPath}/playground/level1/update/${board.boardSeq}.do" class="btn">수정</a>
            <form action="${pageContext.request.contextPath}/playground/level1/delete.do" method="post" onsubmit="return confirm('정말로 삭제하시겠습니까?');" style="display: inline;">
                <input type="hidden" name="boardSeq" value="${board.boardSeq}">
                <button type="submit" class="btn btn-danger">삭제</button>
            </form>
        </c:if>
    </div>
</div>

<%@ include file="/common/footer.jsp" %>