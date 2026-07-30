package com.baseline.controller.api;

import java.io.IOException;
import com.baseline.controller.Action;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class FeedbackAction implements Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        String type = request.getParameter("type");
        String title = request.getParameter("title");
        String message = request.getParameter("message");
        String executedData = request.getParameter("executedData");

        if (type == null || type.isEmpty()) {
            type = "GENERAL";
        }
        if (title == null || title.isEmpty()) {
            title = "공격 성공";
        }
        if (message == null || message.isEmpty()) {
            message = "취약점 조건이 충족되었습니다.";
        }

        HttpSession session = request.getSession(true);

        session.setAttribute("feedback_hasFeedback", true);
        session.setAttribute("feedback_type", type);
        session.setAttribute("feedback_title", title);
        session.setAttribute("feedback_message", message);
        session.setAttribute("feedback_executedData", executedData);

        response.setStatus(HttpServletResponse.SC_OK);
        // JSON 응답 포맷 구성
        String jsonResponse = String.format("{\"status\":\"success\", \"type\":\"%s\", \"message\":\"%s\"}", type, message);
        response.getWriter().write(jsonResponse);
    }
}