package com.baseline.controller.playground.level2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import com.baseline.controller.Action;
import com.baseline.dao.BoardDAO;
import com.baseline.dao.FileDAO;
import com.baseline.util.NotificationUtil;
import com.baseline.dto.BoardVO;
import com.baseline.dto.FileVO;
import com.baseline.dto.UserVO;
import com.baseline.util.FeedbackUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

public class BoardWriteActionLevel2 implements Action {

    // 악성파일 탐지를 위한 확장자 목록 (Level 1과 동일)
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
            String originalTitle = request.getParameter("title");
            String originalContent = request.getParameter("content");

            // 입력값 검증 예시
            if (originalTitle == null || originalTitle.trim().isEmpty()) {
                NotificationUtil.addNotification(request, "제목은 필수 입력 항목입니다.", "error");
                response.sendRedirect(request.getContextPath() + "/playground/level2/write.do");
                return;
            }

            // Level 2 보안: 단순 문자열 치환을 통한 XSS 방어
            String sanitizedTitle = sanitizeXSS(originalTitle);
            String sanitizedContent = sanitizeXSS(originalContent);

            BoardVO boardVO = new BoardVO();
            boardVO.setBoardTitle(sanitizedTitle);
            boardVO.setBoardContents(sanitizedContent);
            boardVO.setBoardAuthor(loginUser.getUserNm());
            boardVO.setBoardLevel(2);

            BoardDAO boardDAO = BoardDAO.getInstance();
            int boardSeq = boardDAO.insertBoard(boardVO);

            if (boardSeq > 0) {
                Part filePart = request.getPart("file");
                if (filePart != null && filePart.getSize() > 0) {
                    String originalFileName = filePart.getSubmittedFileName();

                    // Path Traversal 또는 Webshell 업로드 공격 탐지 및 피드백 세션 세팅 (Level 1과 동일)
                    if (originalFileName != null) {
                        String lowerCaseFileName = originalFileName.toLowerCase();
                        if (lowerCaseFileName.contains("../") || lowerCaseFileName.contains("..\\")) {
                            FeedbackUtil.setFeedback(request, "PATH_TRAVERSAL", "Path Traversal 공격 성공",
                                    "파일 업로드 시 경로 조작(Path Traversal)을 통해 서버의 비공개 경로에 접근했습니다.",
                                    "업로드 시도 파일명: " + originalFileName);
                        } else if (isWebshell(originalFileName)) {
                            FeedbackUtil.setFeedback(request, "FILE_UPLOAD", "악성파일 업로드 성공", // Webshell 업로드는 여전히 피드백
                                    "실행 가능한 스크립트 파일이 업로드되었습니다.", "업로드된 파일명: " + originalFileName); 
                        }
                    }

                    // Level 2의 취약한 필터링
                    String ext = "";
                    if (originalFileName.lastIndexOf(".") != -1) {
                        ext = originalFileName.substring(originalFileName.lastIndexOf("."));
                    }
                    if (ext.equals(".jsp") || ext.equals(".php") || ext.equals(".exe")) {
                        // FeedbackUtil 대신 브라우저 alert를 위해 쿼리 파라미터로 에러 전달
                        response.sendRedirect(request.getContextPath() + "/playground/level2/write.do?error=invalidFileExtension");
                        return;
                    }
                    originalFileName = originalFileName.replace("../", "").replace("..\\", "");

                    String uploadPath = request.getServletContext().getRealPath("/uploads");
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists())
                        uploadDir.mkdirs();

                    File targetFile = new File(uploadPath, originalFileName);
                    if (targetFile.getParentFile() != null && !targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }

                    try (InputStream fileContent = filePart.getInputStream();
                            FileOutputStream fos = new FileOutputStream(targetFile)) {
                        fileContent.transferTo(fos);
                    }

                    FileDAO fileDAO = FileDAO.getInstance();
                    fileDAO.insertFile(new FileVO(boardSeq, originalFileName, originalFileName));
                }
            }
            NotificationUtil.addNotification(request, "게시글이 성공적으로 등록되었습니다.", "success");
            response.sendRedirect(request.getContextPath() + "/playground/level2/view/" + boardSeq + ".do");
        } catch (Exception e) {
            // Path Traversal 공격이 OS 권한에 의해 막혔을 때 발생하는 예외 감지 및 피드백 처리 (Level 1과 동일)
            boolean isPathTraversalError = false;
            String errorMessage = "";
            String originalFileName = "";
            try {
                Part filePart = request.getPart("file");
                if (filePart != null)
                    originalFileName = filePart.getSubmittedFileName();
            } catch (Exception fileEx) {
                // 파일 파싱 중 오류 발생 시 무시
            }

            Throwable cause = e;
            while (cause != null) {
                if (cause instanceof java.io.FileNotFoundException && cause.getMessage() != null
                        && cause.getMessage().contains("액세스가 거부되었습니다")) {
                    isPathTraversalError = true;
                    errorMessage = cause.getMessage();
                    break;
                }
                cause = cause.getCause();
            }

            if (isPathTraversalError && originalFileName != null
                    && (originalFileName.contains("../") || originalFileName.contains("..\\"))) {
                StringBuilder feedbackExecutedData = new StringBuilder();
                feedbackExecutedData.append("업로드 시도 파일명:\n").append(originalFileName).append("\n\n");
                feedbackExecutedData.append("OS 오류 메시지:\n").append(errorMessage);

                FeedbackUtil.setFeedback(request, "PATH_TRAVERSAL", "Path Traversal 공격 성공", // OS가 막았더라도 공격 시도 자체를 성공으로
                                                                                            // 간주
                        "경로 조작을 통해 시스템 보호 디렉터리에 파일 쓰기를 시도했으며, 운영체제(OS)에 의해 접근이 거부되었습니다.",
                        feedbackExecutedData.toString());
                response.sendRedirect(request.getContextPath() + "/playground/level2/list.do");
                return;
            }
            NotificationUtil.addNotification(request, "게시글 작성 중 오류가 발생했습니다: " + e.getMessage(), "error");
            response.sendRedirect(request.getContextPath() + "/playground/level2/write.do");
        }
    }

    private String sanitizeXSS(String input) {
        if (input == null)
            return null;
        // 의도된 결함: 한 번만 치환하므로 중첩 우회 가능. 대소문자 무시 플래그 없음.
        return input.replace("script", "")
                .replace("alert", "")
                .replace("prompt", "")
                .replace("confirm", "")
                .replace("console", "")
                .replace("window", "")
                .replace("cookie", "")
                .replace("session", "")
                .replace("onclick", "")
                .replace("onerror", "")
                .replace("javascript:", "");
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