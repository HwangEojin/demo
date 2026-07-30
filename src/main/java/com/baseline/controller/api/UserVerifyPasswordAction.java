package com.baseline.controller.api;

import java.io.IOException;

import com.baseline.controller.Action;
import com.baseline.dao.UserDAO;
import com.baseline.dto.UserVO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class UserVerifyPasswordAction implements Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        UserVO loginUser = (session != null) ? (UserVO) session.getAttribute("loginUser") : null;

        if (loginUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\": false, \"message\": \"로그인이 필요합니다.\"}");
            return;
        }

        String userPw = request.getParameter("userPw");
        if (userPw == null || userPw.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\": false, \"message\": \"비밀번호를 입력해주세요.\"}");
            return;
        }

        UserDAO userDAO = UserDAO.getInstance();
        UserVO verifiedUser = userDAO.login(loginUser.getUserId(), userPw);

        if (verifiedUser != null) {
            response.getWriter().write("{\"success\": true}");
        } else {
            response.getWriter().write("{\"success\": false, \"message\": \"비밀번호가 일치하지 않습니다.\"}");
        }
    }
}
