package com.baseline.controller.playground.level1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import com.baseline.controller.Action;
import com.baseline.util.NotificationUtil;
import com.baseline.util.FeedbackUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FileDownloadActionLevel1 implements Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            //  DB 시퀀스가 아닌, 클라이언트가 조작 가능한 파라미터를 직접 받아 파일 경로에 결합
            String fileName = request.getParameter("fileName");
            
            if (fileName == null || fileName.isEmpty()) {
                NotificationUtil.addNotification(request, "잘못된 요청입니다. 파일 이름이 없습니다.", "error");
                response.sendRedirect(request.getHeader("Referer"));
                return;
            }

            // Path Traversal 공격 패턴 감지 및 피드백 트리거
            if (fileName.contains("../") || fileName.contains("..\\")) {
                FeedbackUtil.setFeedback(request, "PATH_TRAVERSAL", "Path Traversal 공격 성공", 
                    "상위 디렉터리 접근 패턴(../)을 통해 허가되지 않은 시스템 파일 경로 조작이 감지되었습니다.", "요청된 파라미터: " + fileName);
            }

            String uploadPath = request.getServletContext().getRealPath("/uploads");
            // 입력값을 검증 없이 파일 객체 생성에 사용
            File file = new File(uploadPath, fileName);

            if (file.exists()) {
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(file.getName(), "UTF-8") + "\"");
                
                try (FileInputStream fis = new FileInputStream(file);
                     OutputStream os = response.getOutputStream()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
            } else {
                NotificationUtil.addNotification(request, "요청한 파일을 찾을 수 없습니다.", "error");
                response.sendRedirect(request.getHeader("Referer"));
            }
        } catch (Exception e) {
            NotificationUtil.addNotification(request, "파일 다운로드 중 오류가 발생했습니다: " + e.getMessage(), "error");
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.isEmpty()) {
                response.sendRedirect(referer);
            } else {
                response.sendRedirect(request.getContextPath() + "/playground/level1/list.do");
            }
        }
    }
}