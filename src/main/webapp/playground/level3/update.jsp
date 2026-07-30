<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="pageTitle" value="글 수정" scope="request" />
<%@ include file="/common/header.jsp" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/playground.css">
<script
  id="bs-crypto-script"
  src="${pageContext.request.contextPath}/js/BSCrypto.js"
></script>

<div class="board-container">
    <div class="board-header">
        <h2>글 수정</h2>
        <input type="hidden" id="bsNonce" value="${bsNonce}">
    </div>
    <form action="${pageContext.request.contextPath}/playground/level3/update.do" method="post" class="form-container" enctype="multipart/form-data" data-encrypt="true" data-encrypt-mode="write">
        
        <input type="hidden" name="boardSeq" value="${board.boardSeq}">
        <div class="form-group">
            <label for="title">제목</label>
            <input type="text" id="title" name="title" value="${board.boardTitle}" required>
        </div>
        <div class="form-group">
            <label for="content">내용</label>
            <textarea id="content" name="content" rows="15" required>${board.boardContents}</textarea>
        </div>


        <div class="form-group attachment-manager">
            <label>첨부파일 관리</label>
            <div class="attachment-box">
                <div class="current-files">
                    <span class="file-label">현재 등록된 파일</span>
                    <c:choose>
                        <c:when test="${not empty fileList}">
                            <c:forEach items="${fileList}" var="file">
                                <div class="file-item">
                                    <span class="file-name">${file.originalFileName}</span>
                                    <label class="checkbox-label">
                                        <input type="checkbox" name="deleteAttachment" value="true">
                                        <span class="checkbox-text">삭제하기</span>
                                    </label>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <p class="empty-msg">첨부된 파일이 없습니다.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
                
                <div class="new-file-upload">
                    <span class="file-label">새 파일 업로드 (선택 시 기존 파일 대체)</span>
                    <input type="file" id="file" name="file">
                </div>
            </div>
        </div>


        <div class="form-actions">
            <button type="submit" class="btn">수정 완료</button>
            <a href="${pageContext.request.contextPath}/playground/level3/view.do?boardSeq=${board.boardSeq}" class="btn btn-secondary">취소</a>
        </div>
    </form>
</div>

<%@ include file="/common/footer.jsp" %>