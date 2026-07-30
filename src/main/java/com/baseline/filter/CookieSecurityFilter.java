package com.baseline.filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import jakarta.servlet.http.HttpSession;

public class CookieSecurityFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath().isEmpty() ? "/" : req.getContextPath();

        // 1. [핵심 수정] 응답이 커밋되기 전(chain.doFilter 호출 전)에 강제로 쿠키 헤더를 재전송하여 브라우저 속성 덮어쓰기
        HttpSession session = req.getSession(false);
        if (session != null) {
            String existingSessionId = session.getId();
            if (requestURI.contains("/level1/") || requestURI.contains("/level2/")) {
                res.setHeader("Set-Cookie", "JSESSIONID=" + existingSessionId + "; Path=" + contextPath + "; SameSite=Lax");
            } else if (requestURI.contains("/level5/")) {
                res.setHeader("Set-Cookie", "JSESSIONID=" + existingSessionId + "; Path=" + contextPath + "; HttpOnly; Secure; SameSite=Strict");
            } else {
                res.setHeader("Set-Cookie", "JSESSIONID=" + existingSessionId + "; Path=" + contextPath + "; HttpOnly; SameSite=Strict");
            }
        }

        // 2. 신규 세션 발급 시 Tomcat 내부에서 직접 Set-Cookie 헤더를 조작하는 것을 가로챔
        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(res) {
            @Override
            public void addHeader(String name, String value) {
                if ("Set-Cookie".equalsIgnoreCase(name) && value.startsWith("JSESSIONID=")) {
                    value = modifySessionCookie(value, requestURI, contextPath);
                }
                super.addHeader(name, value);
            }

            @Override
            public void setHeader(String name, String value) {
                if ("Set-Cookie".equalsIgnoreCase(name) && value.startsWith("JSESSIONID=")) {
                    value = modifySessionCookie(value, requestURI, contextPath);
                }
                super.setHeader(name, value);
            }
        };

        // 원본 response 대신 wrapper를 체인에 전달
        chain.doFilter(request, wrapper);
    }

    /**
     * 톰캣이 생성한 JSESSIONID 헤더 문자열을 가로채서 속성을 재조립하는 헬퍼 메서드
     */
    private String modifySessionCookie(String originalHeader, String requestURI, String contextPath) {
        String sessionIdPart = originalHeader.split(";")[0]; 

        if (requestURI.contains("/level1/") || requestURI.contains("/level2/")) {
            return sessionIdPart + "; Path=" + contextPath + "; SameSite=Lax";
        } else if (requestURI.contains("/level5/")) {
            return sessionIdPart + "; Path=" + contextPath + "; HttpOnly; Secure; SameSite=Strict";
        } else {
            return sessionIdPart + "; Path=" + contextPath + "; HttpOnly; SameSite=Strict";
        }
    }

    @Override
    public void destroy() {}
}