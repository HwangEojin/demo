package com.baseline.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class NotificationUtil {

    private static final String NOTIFICATION_SESSION_KEY = "flash_notifications";

    /**
     * 세션에 알림 메시지를 추가합니다. 이 메시지는 다음 요청에서 한 번만 표시된 후 사라집니다.
     * @param request HttpServletRequest 객체
     * @param message 표시할 메시지
     * @param type 알림 타입 ('success', 'error', 'info', 'warning')
     */
    @SuppressWarnings("unchecked")
    public static void addNotification(HttpServletRequest request, String message, String type) {
        HttpSession session = request.getSession();
        List<Map<String, String>> notifications = (List<Map<String, String>>) session.getAttribute(NOTIFICATION_SESSION_KEY);
        if (notifications == null) {
            notifications = new ArrayList<>();
        }
        Map<String, String> notification = new HashMap<>();
        notification.put("message", message);
        notification.put("type", type);
        notifications.add(notification);
        session.setAttribute(NOTIFICATION_SESSION_KEY, notifications);
    }
}
