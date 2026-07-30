package com.baseline.controller.playground.level2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.SQLException;
import com.baseline.controller.Action;
import com.baseline.dao.BoardDAO;
import com.baseline.dao.FileDAO;
import com.baseline.dto.BoardVO;
import com.baseline.dto.FileVO;
import com.baseline.dto.UserVO;
import com.baseline.util.FeedbackUtil;
import com.baseline.util.NotificationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FileDownloadActionLevel2 implements Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileSeqStr = request.getParameter("fileSeq");
        
        if (fileSeqStr == null || fileSeqStr.isEmpty()) {
            NotificationUtil.addNotification(request, "잘못된 요청입니다. 파일 번호가 없습니다.", "error");
            response.sendRedirect(request.getHeader("Referer"));
            return;
        }

        try {
            int fileSeq = Integer.parseInt(fileSeqStr);

            FileDAO fileDAO = FileDAO.getInstance();
            FileVO fileVO = fileDAO.getFileByFileSeq(fileSeq);

            if (fileVO != null) {
                BoardDAO boardDAO = BoardDAO.getInstance();
                BoardVO board = boardDAO.selectBoardBySeq(fileVO.getBoardSeq());
                UserVO loginUser = (UserVO) request.getSession().getAttribute("loginUser");

                String uploadPath = request.getServletContext().getRealPath("/uploads");
                // Level 2는 Path Traversal 필터링이 취약하므로, fileVO.getSavedFileName()을 직접 사용
                // savedFileName에 Path Traversal 페이로드가 포함될 경우 여전히 취약
                File file = new File(uploadPath, fileVO.getSavedFileName());

                if (file.exists()) {
                    response.setContentType("application/octet-stream");
                    // 원본 파일명으로 다운로드되도록 설정
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(fileVO.getOriginalFileName(), "UTF-8") + "\"");
                    
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
            } else {
                NotificationUtil.addNotification(request, "요청한 파일 정보를 찾을 수 없습니다.", "error");
                response.sendRedirect(request.getHeader("Referer"));
            }
        } catch (NumberFormatException e) {
            NotificationUtil.addNotification(request, "잘못된 파일 번호 형식입니다.", "error");
            response.sendRedirect(request.getHeader("Referer"));
        } catch (SQLException e) {
            NotificationUtil.addNotification(request, "데이터베이스 오류가 발생했습니다.", "error");
            response.sendRedirect(request.getHeader("Referer"));
        } catch (Exception e) {
            NotificationUtil.addNotification(request, "파일 다운로드 중 오류가 발생했습니다: " + e.getMessage(), "error");
            response.sendRedirect(request.getHeader("Referer"));
        }
    }
}