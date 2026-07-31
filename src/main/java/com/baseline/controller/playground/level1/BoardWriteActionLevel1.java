package com.baseline.controller.playground.level1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import com.baseline.controller.Action;
import com.baseline.dao.BoardDAO;
import com.baseline.dao.FileDAO;
import com.baseline.util.FeedbackUtil;
import com.baseline.util.NotificationUtil;
import com.baseline.dto.BoardVO;
import com.baseline.dto.FileVO;
import com.baseline.dto.UserVO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

public class BoardWriteActionLevel1 implements Action {

    // 악성파일 탐지를 위한 확장자 목록
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
        String originalFileName = "";
        try {
            HttpSession session = request.getSession();
            UserVO loginUser = (UserVO) session.getAttribute("loginUser");

            if (loginUser == null) {
                response.sendRedirect(request.getContextPath() + "/user/login.do");
                return;
            }
            String title = request.getParameter("title");
            String content = request.getParameter("content");

            Part filePart = request.getPart("file");

            // 0바이트 파일 업로드 체크
            if (filePart != null && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().trim().isEmpty() && filePart.getSize() == 0) {
                NotificationUtil.addNotification(request, "용량이 0인 파일은 업로드할 수 없습니다.", "error");
                response.sendRedirect(request.getContextPath() + "/playground/level1/write.do");
                return;
            }


            BoardVO boardVO = new BoardVO();
            boardVO.setBoardTitle(title);
            boardVO.setBoardContents(content);
            boardVO.setBoardAuthor(loginUser.getUserNm());
            boardVO.setBoardLevel(1);

            BoardDAO boardDAO = BoardDAO.getInstance();
            int boardSeq = boardDAO.insertBoard(boardVO);

            // 파일 업로드 처리
            if (boardSeq > 0) {
                if (filePart != null && filePart.getSize() > 0) {
                    // 사용자가 제출한 파일명을 그대로 사용 (Path Traversal 취약점 포인트)
                    originalFileName = filePart.getSubmittedFileName();

                    // Path Traversal 또는 Webshell 업로드 공격 탐지 및 피드백 세션 세팅
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

                    String uploadPath = request.getServletContext().getRealPath("/uploads");

                    // 디렉터리 준비
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }

                    String savedFileName = originalFileName;
                    File targetFile = new File(uploadPath, savedFileName);

                    // 디렉터리 조작 구문이 들어왔을 경우 부모 디렉터리 생성 지원
                    if (targetFile.getParentFile() != null && !targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }

                    // 파일 저장
                    try (InputStream fileContent = filePart.getInputStream();
                         FileOutputStream fos = new FileOutputStream(targetFile)) {
                        fileContent.transferTo(fos);
                    }

                    // DB에는 사용자가 전송한 원본 파일명과 실제 저장된 파일명을 기록
                    FileVO fileVO = new FileVO();
                    fileVO.setBoardSeq(boardSeq);
                    fileVO.setOriginalFileName(originalFileName);
                    fileVO.setSavedFileName(savedFileName);

                    FileDAO fileDAO = FileDAO.getInstance();
                    fileDAO.insertFile(fileVO);
                }
            }

            NotificationUtil.addNotification(request, "게시글이 성공적으로 등록되었습니다.", "success");
            response.sendRedirect(request.getContextPath() + "/playground/level1/view/" + boardSeq + ".do");
        } catch (Exception e) {
            // Path Traversal 공격이 OS 권한에 의해 막혔을 때 발생하는 예외 감지 및 피드백 처리
            boolean isPathTraversalError = false;
            String errorMessage = "";
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
                response.sendRedirect(request.getContextPath() + "/playground/level1/list.do");
                return;
            }

            // 그 외 일반 오류 처리
            NotificationUtil.addNotification(request, "게시글 작성 중 오류가 발생했습니다: " + e.getMessage(), "error");
            response.sendRedirect(request.getContextPath() + "/playground/level1/write.do");
        }
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