package com.baseline.controller;

import java.io.IOException;
import com.baseline.dao.UserDAO;
import com.baseline.dto.UserVO;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
            response.sendRedirect(request.getContextPath() + "/main.jsp");
        } else { // 로그인 실패
            request.setAttribute("message", "로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("user/login.jsp");
            dispatcher.forward(request, response);
        }
    }
}