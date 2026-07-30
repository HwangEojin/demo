<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="pageTitle" value="사용자 관리" scope="request" />
<%@ include file="/common/header.jsp" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">

<div class="admin-container">
    <div class="admin-header">
        <h2>사용자 관리</h2>
    </div>

    <div class="search-container">
        <form action="${pageContext.request.contextPath}/admin/user_manage.do" method="get" class="search-form" data-encrypt="true">
            
            <select name="searchType">
                <option value="userId" ${searchType == 'userId' ? 'selected' : ''}>아이디</option>
                <option value="userNm" ${searchType == 'userNm' ? 'selected' : ''}>이름</option>
            </select>
            <input type="text" name="keyword" placeholder="검색어를 입력하세요" value="${keyword != null ? keyword : ''}">
            <button type="submit" class="btn btn-search">검색</button>
        </form>
    </div>

    <table class="admin-table">
        <thead>
            <tr>
                <th class="col-seq">번호</th>
                <th class="col-id">아이디</th>
                <th class="col-name">이름</th>
                <th class="col-email">이메일</th>
                <th class="col-num">연락처</th>
                <th class="col-auth">권한</th>
                <th class="col-manage">관리</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty userList}">
                    <tr>
                        <td colspan="7">등록된 사용자가 없습니다.</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${userList}" var="user" varStatus="status">
                        <tr>
                            <td>${pagination.totalCount - ((pagination.currentPage - 1) * pagination.pageSize) - status.index}</td>
                            <td><c:out value="${user.userId}"/></td>
                            <td><c:out value="${user.userNm}"/></td>
                            <td><c:out value="${user.userEmail}"/></td>
                            <td><c:out value="${user.userNum}"/></td>
                            <td><strong>${user.userTp == 'A' ? '관리자' : '일반'}</strong></td>
                            <td>
                                <c:if test="${sessionScope.loginUser.userSeq != user.userSeq}">
                                    <form action="${pageContext.request.contextPath}/admin/update_auth.do" method="post" class="admin-btn-form" data-encrypt="true">
                                        
                                        <input type="hidden" name="userSeq" value="${user.userSeq}">
                                        <input type="hidden" name="newAuth" value="${user.userTp == 'A' ? 'B' : 'A'}">
                                        <input type="hidden" name="page" value="${pagination.currentPage}">
                                        <button type="submit" class="btn btn-sm">${user.userTp == 'A' ? '일반으로 변경' : '관리자로 변경'}</button>
                                    </form>
                                    <form action="${pageContext.request.contextPath}/admin/delete_user.do" method="post" class="admin-btn-form" data-encrypt="true" data-confirm-message="정말로 이 사용자를 삭제하시겠습니까?">
                                        
                                        <input type="hidden" name="userSeq" value="${user.userSeq}">
                                        <input type="hidden" name="page" value="${pagination.currentPage}">
                                        <button type="submit" class="btn btn-sm btn-danger">삭제</button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>

    <div class="pagination">
        <c:set var="searchParams" value="${(not empty searchType and not empty keyword) ? '&searchType='.concat(searchType).concat('&keyword=').concat(keyword) : ''}" />
        <c:if test="${pagination.prev}">
            <a href="${pageContext.request.contextPath}/admin/user_manage.do?page=${pagination.startPage - 1}${searchParams}">&lt;</a>
        </c:if>

        <c:forEach begin="${pagination.startPage}" end="${pagination.endPage}" var="pageNum">
            <c:choose>
                <c:when test="${pageNum == pagination.currentPage}">
                    <strong class="current">${pageNum}</strong>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/admin/user_manage.do?page=${pageNum}${searchParams}">${pageNum}</a>
                </c:otherwise>
            </c:choose>
        </c:forEach>

        <c:if test="${pagination.next}">
            <a href="${pageContext.request.contextPath}/admin/user_manage.do?page=${pagination.endPage + 1}${searchParams}">&gt;</a>
        </c:if>
    </div>
</div>

<%@ include file="/common/footer.jsp" %>