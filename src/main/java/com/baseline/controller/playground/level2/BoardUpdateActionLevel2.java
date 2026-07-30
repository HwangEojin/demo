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
import com.baseline.dto.BoardVO;
import com.baseline.dto.FileVO;
import com.baseline.dto.UserVO;
import com.baseline.util.FeedbackUtil;
import com.baseline.util.NotificationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

public class BoardUpdateActionLevel2 implements Action {
    // 악성파일 탐지를 위한 확장자 목록 (Level 1과 동일)
    private static final List<String> WEBSHELL_EXTENSIONS = Arrays.asList(
        // Java
        ".jsp", ".jspx", ".jsw", ".jsv", ".jspf", ".war",
        // PHP
        ".php", ".phtml", ".php3", ".php4", ".php5",
        // ASP/ASP.NET
        ".asp", ".aspx", ".ashx", ".asmx", ".ascx", ".cer",
        // 기타 스크립트
        ".sh", ".bat", ".pl", ".cgi", ".py", ".rb"
    );

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int boardSeq = Integer.parseInt(request.getParameter("boardSeq"));
            String originalTitle = request.getParameter("title");
            String originalContent = request.getParameter("content");
            
            BoardDAO boardDAO = BoardDAO.getInstance();
            BoardVO board = boardDAO.selectBoardBySeq(boardSeq);

            if (board == null) {
                NotificationUtil.addNotification(request, "수정할 게시글이 존재하지 않습니다.", "error");
                response.sendRedirect(request.getContextPath() + "/playground/level2/list.do");
                return;
            }
    
            UserVO loginUser = (UserVO) request.getSession().getAttribute("loginUser");
            if (loginUser == null) {
                NotificationUtil.addNotification(request, "로그인이 필요합니다.", "error");
                response.sendRedirect(request.getContextPath() + "/user/login.do?targetUrl=" + request.getRequestURI());
                return;
            }

            // Level 2 보안: 작성자 확인 로직 적용
            boolean isAuthor = loginUser.getUserNm().equals(board.getBoardAuthor());

            // Level 2: 권한 검사 없이 타인 글 수정 시 IDOR 공격 성공 피드백
            if (!isAuthor) {
                FeedbackUtil.setFeedback(
                    request,
                    "IDOR",
                    "IDOR 공격 성공",
                    "타 사용자의 게시글을 권한 없이 수정하는 데 성공했습니다.", 
                    "게시글 번호: " + boardSeq + ", 작성자: " + board.getBoardAuthor() + ", 시도 사용자: " + loginUser.getUserNm()
                );
            }

            String sanitizedTitle = sanitizeXSS(originalTitle);
            String sanitizedContent = sanitizeXSS(originalContent);

            board.setBoardTitle(sanitizedTitle);
            board.setBoardContents(sanitizedContent);
            boardDAO.updateBoard(board);
    
            Part filePart = request.getPart("file");
            if (filePart != null && filePart.getSize() > 0) {
                String originalFileName = filePart.getSubmittedFileName(); // 사용자가 제출한 파일명을 그대로 사용

                // Path Traversal 또는 Webshell 업로드 공격 탐지 및 피드백 세션 세팅 (Level 1과 동일)
                if (originalFileName != null) {
                    String lowerCaseFileName = originalFileName.toLowerCase();
                    if (lowerCaseFileName.contains("../") || lowerCaseFileName.contains("..\\")) {
                        FeedbackUtil.setFeedback(request, "PATH_TRAVERSAL", "Path Traversal 공격 성공",
                                "파일 업로드 시 경로 조작(Path Traversal)을 통해 서버의 비공개 경로에 접근했습니다.", "업로드 시도 파일명: " + originalFileName);
                    } else if (isWebshell(originalFileName)) {
                        FeedbackUtil.setFeedback(request, "FILE_UPLOAD", "악성파일 업로드 성공",
                                "실행 가능한 스크립트 파일이 업로드되었습니다.", "업로드된 파일명: " + originalFileName); 
                    }
                }

                // Level 2 보안: 블랙리스트 기반 파일 확장자 차단
                // 중/고급 취약점: 대소문자 미구분, 파생 확장자 허용 (예: .jspx, .JSP, .php5 등 업로드 가능)
                String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
                if (ext.equals(".jsp") || ext.equals(".php") || ext.equals(".exe")) {
                    NotificationUtil.addNotification(request, "허용되지 않는 파일 확장자입니다: " + originalFileName, "error");
                    response.sendRedirect(request.getContextPath() + "/playground/level2/update/" + boardSeq + ".do");
                    return;
                }

                // Level 2 보안: 상위 디렉터리 접근 문자열 필터링 (취약)
                // 중/고급 취약점: replace 메소드는 한 번만 동작하므로 "....//" 입력 시 "../"가 남아 우회됨.
                // 추가로 URL 인코딩(%2e%2e%2f)에 대한 디코딩 후 검증 과정 누락
                originalFileName = originalFileName.replace("../", "").replace("..\\", "");

                String uploadPath = request.getServletContext().getRealPath("/uploads");
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                File targetFile = new File(uploadPath, originalFileName); 

                try (InputStream fileContent = filePart.getInputStream();
                     FileOutputStream fos = new FileOutputStream(targetFile)) {
                    fileContent.transferTo(fos);
                }

                FileDAO fileDAO = FileDAO.getInstance();
                fileDAO.insertFile(new FileVO(boardSeq, originalFileName, originalFileName));
            }
    
            NotificationUtil.addNotification(request, "게시글이 성공적으로 수정되었습니다.", "success");
            response.sendRedirect(request.getContextPath() + "/playground/level2/view/" + boardSeq + ".do");
        } catch (Exception e) {
            // Path Traversal 공격이 OS 권한에 의해 막혔을 때 발생하는 예외 감지 및 피드백 처리 (Level 1과 동일)
            boolean isPathTraversalError = false;
            String errorMessage = "";
            String originalFileName = request.getPart("file") != null ? request.getPart("file").getSubmittedFileName() : "";
            Throwable cause = e;
            while (cause != null) {
                if (cause instanceof java.io.FileNotFoundException && cause.getMessage() != null && cause.getMessage().contains("액세스가 거부되었습니다")) {
                    isPathTraversalError = true;
                    errorMessage = cause.getMessage();
                    break;
                }
                cause = cause.getCause();
            }

            if (isPathTraversalError && originalFileName != null && (originalFileName.contains("../") || originalFileName.contains("..\\"))) {
                StringBuilder feedbackExecutedData = new StringBuilder();
                feedbackExecutedData.append("업로드 시도 파일명:\n").append(originalFileName).append("\n\n");
                feedbackExecutedData.append("OS 오류 메시지:\n").append(errorMessage);
                
                FeedbackUtil.setFeedback(request, "PATH_TRAVERSAL", "Path Traversal 공격 성공", // OS가 막았더라도 공격 시도 자체를 성공으로 간주
                        "경로 조작을 통해 시스템 보호 디렉터리에 파일 쓰기를 시도했으며, 운영체제(OS)에 의해 접근이 거부되었습니다.", feedbackExecutedData.toString());
                response.sendRedirect(request.getContextPath() + "/playground/level2/list.do");
                return;
            }

            // 그 외 일반 오류 처리
            NotificationUtil.addNotification(request, "게시글 수정 중 오류가 발생했습니다: " + e.getMessage(), "error");
            response.sendRedirect(request.getContextPath() + "/playground/level2/list.do");
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
        if (fileName == null) return false;
        String lowerCaseFileName = fileName.toLowerCase();
        for (String ext : WEBSHELL_EXTENSIONS) {
            if (lowerCaseFileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}