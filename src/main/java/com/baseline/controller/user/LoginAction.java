package com.baseline.controller.user;

import java.io.IOException;
import com.baseline.controller.Action;
import com.baseline.dao.UserDAO;
import com.baseline.dto.UserVO;
import com.baseline.util.NotificationUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginAction implements Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getParameter("userId");
        String userPw = request.getParameter("userPw");

        UserDAO userDAO = UserDAO.getInstance();
        UserVO userVO = userDAO.login(userId, userPw);

        if (userVO != null) { // 로그인 성공
            HttpSession session = request.getSession();
            session.setAttribute("loginUser", userVO);

            String targetUrl = (String) session.getAttribute("targetUrl");
            session.removeAttribute("targetUrl"); 

            // targetUrl이 API 요청 경로인 경우, 메인 페이지로 리다이렉트하여 JSON이 화면에 직접 표시되는 것을 방지합니다.
            if (targetUrl != null && !targetUrl.isEmpty() && !targetUrl.contains("/api/")) {
                response.sendRedirect(targetUrl);
            } else {
                response.sendRedirect(request.getContextPath() + "/");
            }
        } else { // 로그인 실패
            NotificationUtil.addNotification(request, "로그인에 실패했습니다.\n아이디와 비밀번호를 확인해주세요.", "error");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/user/login.jsp");
            dispatcher.forward(request, response);
        }
    }
}