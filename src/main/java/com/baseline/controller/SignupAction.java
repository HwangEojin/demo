package com.baseline.controller;

import java.io.IOException;

import com.baseline.dao.UserDAO;
import com.baseline.dto.UserVO;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SignupAction implements Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserVO userVO = new UserVO();
        userVO.setUserId(request.getParameter("userId"));
        userVO.setUserPw(request.getParameter("userPw"));
        userVO.setUserNm(request.getParameter("userNm"));
        userVO.setUserNum(request.getParameter("userNum"));
        userVO.setUserEmail(request.getParameter("userEmail"));

        UserDAO userDAO = UserDAO.getInstance();
        int result = userDAO.signup(userVO);

        if (result > 0) { // 회원가입 성공
            request.getSession().setAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
            response.sendRedirect("login_form.do");
        } else { // 회원가입 실패
            request.setAttribute("message", "회원가입에 실패했습니다. 다시 시도해주세요.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("user/signup.jsp");
            dispatcher.forward(request, response);
        }
    }
}