package com.baseline.controller.user;

import java.io.IOException;

import com.baseline.controller.Action;
import com.baseline.util.NotificationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MyPageFormAction implements Action {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.getRequestDispatcher("/user/mypage.jsp").forward(request, response);
        } catch (Exception e) {
            NotificationUtil.addNotification(request, "마이페이지를 불러오는 중 오류가 발생했습니다.", "error");
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}