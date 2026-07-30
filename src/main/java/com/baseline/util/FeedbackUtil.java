package com.baseline.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class FeedbackUtil {

    /**
     * 모든 컨트롤러에서 공통으로 사용할 표준화된 피드백 세션 설정 메서드
     * @param request HttpServletRequest 객체
     * @param type 취약점 타입 (e.g., "XSS", "IDOR")
     * @param title 피드백 모달에 표시될 제목
     * @param message 피드백 모달에 표시될 상세 메시지
     * @param executedData 공격에 사용된 페이로드 또는 결과 데이터
     */
    public static void setFeedback(HttpServletRequest request, String type, String title, String message, String executedData) {
        HttpSession session = request.getSession(true);
        session.setAttribute("feedback_hasFeedback", true);
        session.setAttribute("feedback_type", type);
        session.setAttribute("feedback_title", title);
        session.setAttribute("feedback_message", message);
        session.setAttribute("feedback_executedData", executedData);
    }
}