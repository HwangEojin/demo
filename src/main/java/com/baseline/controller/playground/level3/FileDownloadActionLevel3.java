package com.baseline.controller.playground.level3;

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

public class FileDownloadActionLevel3 implements Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileSeqStr = (String) request.getAttribute("fileSeq");
        if (fileSeqStr == null) {
            fileSeqStr = request.getParameter("fileSeq");
        }
        
        if (fileSeqStr == null || fileSeqStr.isEmpty()) {
            NotificationUtil.addNotification(request, "잘못된 요청입니다. 파일 번호가 없습니다.", "error");
            response.sendRedirect(request.getContextPath());
            return;
        }

        try {
            int fileSeq = Integer.parseInt(fileSeqStr);

            FileDAO fileDAO = FileDAO.getInstance();
            FileVO fileVO = fileDAO.getFileByFileSeq(fileSeq);
            BoardVO board = null;

            if (fileVO != null) {
                BoardDAO boardDAO = BoardDAO.getInstance();
                board = boardDAO.selectBoardBySeq(fileVO.getBoardSeq());
                UserVO loginUser = (UserVO) request.getSession().getAttribute("loginUser");

                String uploadPath = request.getServletContext().getRealPath("/uploads");
                File file = new File(uploadPath, fileVO.getSavedFileName());

                // Path Traversal 공격 탐지
                File uploadDir = new File(uploadPath);
                try {
                    String canonicalPath = file.getCanonicalPath();
                    String canonicalUploadPath = uploadDir.getCanonicalPath();
                    if (!canonicalPath.startsWith(canonicalUploadPath)) {
                        FeedbackUtil.setFeedback(request, "PATH_TRAVERSAL", "Path Traversal 공격 성공",
                                "경로 조작을 통해 서버의 다른 경로에 있는 파일 다운로드에 성공했습니다.",
                                "요청 파일: " + fileVO.getSavedFileName());
                    }
                } catch (IOException e) {
                    // 정식 경로를 확인하는 과정에서 오류가 발생할 수 있음
                }

                if (file.exists()) {
                    response.setContentType("application/octet-stream");
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
                    response.sendRedirect(request.getContextPath() + "/playground/level" + board.getBoardLevel() + "/list.do");
                }
            } else {
                NotificationUtil.addNotification(request, "요청한 파일 정보를 찾을 수 없습니다.", "error");
                response.sendRedirect(request.getContextPath());
            }
        } catch (NumberFormatException e) {
            NotificationUtil.addNotification(request, "잘못된 파일 번호 형식입니다.", "error");
            response.sendRedirect(request.getContextPath());
        } catch (SQLException e) {
            NotificationUtil.addNotification(request, "데이터베이스 오류가 발생했습니다.", "error");
            response.sendRedirect(request.getContextPath());
        } catch (Exception e) {
            NotificationUtil.addNotification(request, "파일 다운로드 중 오류가 발생했습니다: " + e.getMessage(), "error");
            response.sendRedirect(request.getContextPath());
        }
    }
}