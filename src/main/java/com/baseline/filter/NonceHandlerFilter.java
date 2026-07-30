package com.baseline.filter;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class NonceHandlerFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        // 1. 비동기 피드백 폴링 등 /api/ 경로는 Nonce 발급 대상에서 제외
        if (requestURI.startsWith(contextPath + "/api/")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. 세션 조회 (없으면 새로 생성하지 않고 null 반환)
        HttpSession session = httpRequest.getSession(false);
        String nonce = null;

        if (session != null) {
            nonce = (String) session.getAttribute("bsNonce");
        }

        // 3. 세션에 Nonce가 없거나 비어있는 경우에만 새로 발급
        if (nonce == null || nonce.isEmpty()) {
            if (session == null) {
                session = httpRequest.getSession(true); // 세션이 없으면 생성
            }
            nonce = UUID.randomUUID().toString();
            session.setAttribute("bsNonce", nonce);
        }

        // 4. request 속성에도 동일하게 담아 뷰(JSP)로 전달
        request.setAttribute("bsNonce", nonce);
        chain.doFilter(request, response);
    }
}