<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="pageTitle" value="PLAYGROUND" scope="request" />
<%@ include file="/common/header.jsp" %>
<link
  rel="stylesheet"
  href="${pageContext.request.contextPath}/css/playground.css"
/>

<div class="playground-container">
  <div class="playground-header">
    <h2>PLAYGROUND</h2>
    <p>단계별 시큐어 코딩 및 웹 취약점 진단 수준(Level 1 ~ 5)을 선택하세요.</p>
  </div>

  <div class="level-selection">
    <div class="level-card">
      <div class="level-card-header">
        <h3>Level 1: 취약 환경</h3>
        <span class="badge badge-high-risk">High Risk</span>
      </div>
      <p>보안 기능이 전무한 가장 취약한 웹 환경입니다.</p>
      <a
        href="${pageContext.request.contextPath}/playground/level1/list.do"
        class="btn"
        >Level 1 시작하기</a
      >
    </div>

    <div class="level-card">
      <div class="level-card-header">
        <h3>Level 2: 기초 검증 환경</h3>
        <span class="badge badge-medium-risk">Medium Risk</span>
      </div>
      <p>
        일부 보안 기능이 적용되었지만, 특정 패턴을 우회하여 다양한 공격이 가능한
        환경입니다.
      </p>
      <a
        href="${pageContext.request.contextPath}/playground/level2/list.do"
        class="btn"
        >Level 2 시작하기</a
      >
    </div>

    <div class="level-card">
      <div class="level-card-header">
        <h3>Level 3: 중간 방어 환경</h3>
        <span class="badge badge-low-risk">Low Risk</span>
      </div>
      <p>
        주요 기술적 취약점은 방어되었으나, 설계상의 논리적 허점이나 불충분한
        검증으로 인해 발생하는 취약점이 발생할 수 있는 환경입니다.
      </p>
      <a
        href="${pageContext.request.contextPath}/playground/level3/list.do"
        class="btn"
        >Level 3 시작하기</a
      >
    </div>

    <div class="level-card">
      <div class="level-card-header">
        <h3>Level 4: 고도화 방어 환경</h3>
        <span class="badge badge-secure">Secure</span>
      </div>
      <p>기본적인 시큐어 코딩 가이드라인이 적용된 웹 환경입니다.</p>
      <a href="#" class="btn btn-disabled" onclick="return false;"
        >Level 4 시작하기 (준비중)</a
      >
    </div>

    <div class="level-card">
      <div class="level-card-header">
        <h3>Level 5: 완전 구현 및 암호화 환경</h3>
        <span class="badge badge-hardened">Hardened</span>
      </div>
      <p>보안 표준을 준수하여 구현된 가장 안전한 수준의 웹 환경입니다.</p>
      <a href="#" class="btn btn-disabled" onclick="return false;"
        >Level 5 시작하기 (준비중)</a
      >
    </div>
  </div>
</div>

<%@ include file="/common/footer.jsp" %>
