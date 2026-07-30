<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="pageTitle" value="글 작성" scope="request" />
<%@ include file="/common/header.jsp" %>
<link
  rel="stylesheet"
  href="${pageContext.request.contextPath}/css/playground.css"
/>
<script
  id="bs-crypto-script"
  src="${pageContext.request.contextPath}/js/BSCrypto.js"
></script>

<div class="board-container">
  <div class="board-header">
    <h2>글 작성</h2>
    <input type="hidden" id="bsNonce" value="${bsNonce}" />
  </div>
  <form
    action="${pageContext.request.contextPath}/playground/level3/write.do"
    method="post"
    class="form-container"
    enctype="multipart/form-data"
    data-encrypt="true"
    data-encrypt-mode="write"
  >
    <div class="form-group">
      <label for="title">제목</label>
      <input type="text" id="title" name="title" required />
    </div>
    <div class="form-group">
      <label for="content">내용</label>
      <textarea id="content" name="content" rows="15" required></textarea>
    </div>
    <div class="form-group">
      <label for="file">첨부파일</label>
      <input type="file" id="file" name="file" />
    </div>
    <div class="form-actions" style="text-align: right">
      <button type="submit" class="btn">등록</button>
      <a
        href="${pageContext.request.contextPath}/playground/level3/list.do"
        class="btn btn-secondary"
        >취소</a
      >
    </div>
  </form>
</div>

<%@ include file="/common/footer.jsp" %>
