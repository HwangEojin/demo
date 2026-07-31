package com.baseline.controller.playground.level3;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.baseline.controller.Action;
import com.baseline.dao.BoardDAO;
import com.baseline.dao.FileDAO;
import com.baseline.util.NotificationUtil;
import com.baseline.util.FeedbackUtil;
import com.baseline.dto.BoardVO;
import com.baseline.dto.FileVO;
import com.baseline.dto.UserVO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

public class BoardWriteActionLevel3 implements Action {

    private static final List<String> WEBSHELL_EXTENSIONS = Arrays.asList(
            ".jsp", ".jspx", ".jsw", ".jsv", ".jspf", ".war",
            ".php", ".phtml", ".php3", ".php4", ".php5",
            ".asp", ".aspx", ".ashx", ".asmx", ".ascx", ".cer",
            ".sh", ".bat", ".pl", ".cgi", ".py", ".rb");


    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserVO loginUser = (UserVO) session.getAttribute("loginUser");

        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.do");
            return;
        }

        try {
            String title = request.getParameter("title");
            String content = request.getParameter("content");
            Part filePart = request.getPart("file");

            // 0바이트 파일 업로드 체크
            if (filePart != null && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().trim().isEmpty() && filePart.getSize() == 0) {
                NotificationUtil.addNotification(request, "용량이 0인 파일은 업로드할 수 없습니다.", "error");
                response.sendRedirect(request.getContextPath() + "/playground/level3/write.do");
                return;
            }


            String uploadPath = request.getServletContext().getRealPath("/uploads");
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String originalFileName = null;
            String savedFileName = null;

            // 파일이 첨부된 경우, 유효성 검사를 먼저 수행합니다.
            if (filePart != null && filePart.getSize() > 0) {
                originalFileName = new File(filePart.getSubmittedFileName()).getName(); // 경로 조작 방지

                if (isWebshell(originalFileName)) {
                    FeedbackUtil.setFeedback(request, "FILE_UPLOAD", "악성파일 업로드 성공",
                            "확장자 검증을 우회하여 웹쉘 업로드에 성공했습니다.", "업로드된 파일명: " + originalFileName);
                }

                String extension = "";
                int dotIndex = originalFileName.lastIndexOf('.');

                if (dotIndex > 0 && dotIndex < originalFileName.length() - 1) {
                    extension = originalFileName.substring(dotIndex + 1).toLowerCase();
                }

                List<String> allowedExtensions = Arrays.asList("txt", "pdf", "png", "jpg", "jpeg", "gif", "zip");
                if (extension.isEmpty() || !allowedExtensions.contains(extension)) {
                    NotificationUtil.addNotification(request, "허용되지 않는 파일 형식(" + extension + ")입니다.", "error");
                    response.sendRedirect(request.getContextPath() + "/playground/level3/write.do");
                    return;
                }
                
                // 의도된 취약점: UUID를 사용하지만, 파일명에 원본 파일명을 포함하여 Null Byte Injection에 취약하게 만듦
                savedFileName = UUID.randomUUID().toString() + "-" + originalFileName;
            }

            // 게시글 정보를 DB에 저장합니다.
            BoardVO boardVO = new BoardVO();
            boardVO.setBoardTitle(title);
            boardVO.setBoardContents(content);
            boardVO.setBoardAuthor(loginUser.getUserNm());
            boardVO.setBoardLevel(3);
            
            BoardDAO boardDAO = BoardDAO.getInstance();
            int boardSeq = boardDAO.insertBoard(boardVO);

            // 게시글 저장이 성공하고, 업로드할 파일이 있는 경우 파일을 저장하고 DB에 기록합니다.
            if (boardSeq > 0 && savedFileName != null) {
                filePart.write(uploadPath + File.separator + savedFileName);
                FileVO fileVO = new FileVO();
                fileVO.setBoardSeq(boardSeq);
                fileVO.setOriginalFileName(originalFileName);
                fileVO.setSavedFileName(savedFileName);
                FileDAO fileDAO = FileDAO.getInstance();
                fileDAO.insertFile(fileVO);
            }
            NotificationUtil.addNotification(request, "게시글이 성공적으로 등록되었습니다.", "success");
            response.sendRedirect(request.getContextPath() + "/playground/level3/view/" + boardSeq + ".do");
        } catch (Exception e) {
            NotificationUtil.addNotification(request, "게시글 작성 중 오류가 발생했습니다: " + e.getMessage(), "error");
            response.sendRedirect(request.getContextPath() + "/playground/level3/write.do");
        }
    }

    private boolean isWebshell(String fileName) {
        if (fileName == null)
            return false;
        String lowerCaseFileName = fileName.toLowerCase();
        for (String ext : WEBSHELL_EXTENSIONS) {
            if (lowerCaseFileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}