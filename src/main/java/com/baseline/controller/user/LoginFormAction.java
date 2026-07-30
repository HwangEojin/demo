package com.baseline.controller.user;

import java.io.IOException;

import com.baseline.controller.Action;
import com.baseline.util.NotificationUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginFormAction implements Action {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/user/login.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            NotificationUtil.addNotification(request, "로그인 페이지를 불러오는 중 오류가 발생했습니다.", "error");
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}