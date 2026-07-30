package com.baseline.controller.playground.level4;

import java.io.IOException;

import com.baseline.controller.Action;
import com.baseline.util.NotificationUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BoardWriteFormActionLevel4 implements Action {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/playground/level4/write.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            NotificationUtil.addNotification(request, "글쓰기 양식을 불러오는 중 오류가 발생했습니다.", "error");
            response.sendRedirect(request.getContextPath() + "/playground/level4/list.do");
        }
    }
}