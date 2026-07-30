<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Level 3 게시판" scope="request" />
<%@ include file="/common/header.jsp" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/playground.css">
<script
  id="bs-crypto-script"
  src="${pageContext.request.contextPath}/js/BSCrypto.js"
></script>

<div class="board-container">
    <div class="board-header">
        <h2>Level 3: 안전한 게시판</h2>
        <input type="hidden" id="bsNonce" value="${bsNonce}">
    </div>

    <div class="search-container">
        <form action="${pageContext.request.contextPath}/playground/level3/list.do" method="get" class="search-form" data-encrypt="true">
            <select name="searchType">
                <option value="title" ${searchType == 'title' ? 'selected' : ''}>제목</option>
                <option value="author" ${searchType == 'author' ? 'selected' : ''}>작성자</option>
            </select>
            <input type="text" name="keyword" placeholder="검색어를 입력하세요" value="${keyword != null ? keyword : ''}">
            <button type="submit" class="btn btn-search">검색</button>
        </form>
    </div>

    <table class="board-table">
        <thead>
            <tr>
                <th class="col-seq">번호</th>
                <th class="col-title">제목</th>
                <th class="col-author">작성자</th>
                <th class="col-date">작성일</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty boardList}">
                    <tr>
                        <td colspan="4">게시글이 없습니다.</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${boardList}" var="board" varStatus="status">
                        <tr>
                            <td>${pagination.totalCount - ((pagination.currentPage - 1) * pagination.pageSize) - status.index}</td>
                            <td class="title">
                                <a href="${pageContext.request.contextPath}/playground/level3/view/${board.boardSeq}.do">${board.boardTitle}</a>
                            </td>
                            <td>${board.boardAuthor}</td>
                            <td><fmt:formatDate value="${board.uploadTime}" pattern="yyyy-MM-dd"/></td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
    <div class="board-table-action">
        <c:if test="${not empty sessionScope.loginUser}">
            <a href="${pageContext.request.contextPath}/playground/level3/write.do" class="btn">글쓰기</a>
        </c:if>
    </div>

    <div class="pagination">
        <c:set var="searchParams" value="${(not empty searchType and not empty keyword) ? '&searchType='.concat(searchType).concat('&keyword=').concat(keyword) : ''}" />
        <c:if test="${pagination.prev}">
            <a href="${pageContext.request.contextPath}/playground/level3/list.do?page=${pagination.startPage - 1}${searchParams}">&lt;</a>
        </c:if>

        <c:forEach begin="${pagination.startPage}" end="${pagination.endPage}" var="pageNum">
            <c:choose>
                <c:when test="${pageNum == pagination.currentPage}">
                    <strong class="current">${pageNum}</strong>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/playground/level3/list.do?page=${pageNum}${searchParams}">${pageNum}</a>
                </c:otherwise>
            </c:choose>
        </c:forEach>

        <c:if test="${pagination.next}">
            <a href="${pageContext.request.contextPath}/playground/level3/list.do?page=${pagination.endPage + 1}${searchParams}">&gt;</a>
        </c:if>
    </div>
</div>


<%@ include file="/common/footer.jsp" %>