package com.baseline.filter;

import java.io.IOException;

import com.baseline.dto.UserVO;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false); // false: 세션이 없으면 새로 생성하지 않음
        String requestURI = httpRequest.getRequestURI();

        boolean isLoggedIn = (session != null && session.getAttribute("loginUser") != null);

        if (isLoggedIn) {
            // 관리자 페이지 접근 제어
            if (requestURI.startsWith(httpRequest.getContextPath() + "/admin/")) {
                UserVO loginUser = (UserVO) session.getAttribute("loginUser");
                if ("A".equals(loginUser.getUserTp())) {
                    chain.doFilter(request, response); // 관리자, 접근 허용
                } else {
                    // 관리자가 아닌 사용자가 관리자 페이지 접근 시
                    httpResponse.setContentType("text/html; charset=UTF-8");
                    java.io.PrintWriter out = httpResponse.getWriter();
                    out.println("<script>alert('관리자만 접근 가능한 페이지입니다.'); history.back();</script>");
                    out.flush();
                }
            } else {
                chain.doFilter(request, response); // 관리자 페이지가 아니면 통과
            }
        } else {
            String contextPath = httpRequest.getContextPath();
            // 로그인, 회원가입 페이지, 관련 API는 비로그인 상태에서도 접근을 허용합니다.
            if (requestURI.equals(contextPath + "/user/login.do") ||
                requestURI.equals(contextPath + "/user/signup.do") ||
                requestURI.startsWith(contextPath + "/api/user/")) { // ID 중복체크 API 등
                chain.doFilter(request, response);
            } else {
                // 그 외의 페이지는 로그인 페이지로 리다이렉트합니다.
                // 사용자가 원래 요청했던 URI와 쿼리 스트링을 세션에 저장합니다.
                String queryString = httpRequest.getQueryString();
                if (queryString != null) {
                    requestURI += "?" + queryString;
                }
                
                // 세션을 새로 생성해서라도 targetUrl을 저장합니다.
                HttpSession sessionForRedirect = httpRequest.getSession(); 
                sessionForRedirect.setAttribute("targetUrl", requestURI);

                // 클라이언트에서 알림창을 띄우고 이동하도록 스크립트를 응답으로 보냅니다.
                httpResponse.setContentType("text/html; charset=UTF-8");
                java.io.PrintWriter out = httpResponse.getWriter();
                out.println("<script>alert('로그인이 필요한 서비스입니다.'); location.href='" + httpRequest.getContextPath() + "/user/login.do';</script>");
                out.flush();
            }
        }
    }

}