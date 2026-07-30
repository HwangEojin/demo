package com.baseline.controller.playground.level1;

import java.io.IOException;
import com.baseline.controller.Action;
import com.baseline.util.NotificationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BoardWriteFormActionLevel1 implements Action {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.getRequestDispatcher("/playground/level1/write.jsp").forward(request, response);
        } catch (Exception e) {
            NotificationUtil.addNotification(request, "글쓰기 양식을 불러오는 중 오류가 발생했습니다.", "error");
            response.sendRedirect(request.getContextPath() + "/playground/level1/list.do");
        }
    }
}