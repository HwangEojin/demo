package com.baseline.controller.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.baseline.controller.Action;
import com.google.gson.Gson; 
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class FeedbackStatusAction implements Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. JSON Content-Type 및 문자셋 설정
        response.setContentType("application/json; charset=UTF-8");
        
        // 2. 브라우저 AJAX 폴링 응답 캐싱 방지 헤더 추가
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);
        Map<String, Object> responseData = new HashMap<>();
        
        if (session != null && session.getAttribute("feedback_type") != null) {
            responseData.put("hasFeedback", true);
            responseData.put("type", session.getAttribute("feedback_type"));
            
            Object title = session.getAttribute("feedback_title");
            responseData.put("title", title != null ? title : "공격 성공");
            
            Object message = session.getAttribute("feedback_message");
            responseData.put("message", message != null ? message : "");
            
            // 모달 내 Triggered API 항목 매핑 보완
            Object triggerApi = session.getAttribute("feedback_triggerApi");
            responseData.put("triggerApi", triggerApi != null ? triggerApi : null);

            Object executedData = session.getAttribute("feedback_executedData");
            responseData.put("executedData", executedData != null ? executedData : "");

            // 1회성 출력을 위한 세션 속성 일괄 제거
            session.removeAttribute("feedback_type");
            session.removeAttribute("feedback_title");
            session.removeAttribute("feedback_message");
            session.removeAttribute("feedback_triggerApi");
            session.removeAttribute("feedback_executedData");
        } else {
            responseData.put("hasFeedback", false);
        }
        
        Gson gson = new Gson();
        out.print(gson.toJson(responseData));
        out.flush();
    }
}