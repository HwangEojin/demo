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
import jakarta.servlet.http.Part;

public class BoardUpdateActionLevel3 implements Action {

    private static final List<String> WEBSHELL_EXTENSIONS = Arrays.asList(
            ".jsp", ".jspx", ".jsw", ".jsv", ".jspf", ".war",
            ".php", ".phtml", ".php3", ".php4", ".php5",
            ".asp", ".aspx", ".ashx", ".asmx", ".ascx", ".cer",
            ".sh", ".bat", ".pl", ".cgi", ".py", ".rb");


    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String boardSeqStr = request.getParameter("boardSeq");
            String title = request.getParameter("title");
            String content = request.getParameter("content");
            boolean deleteExisting = "true".equals(request.getParameter("deleteAttachment"));

            int boardSeq = Integer.parseInt(boardSeqStr);

            UserVO loginUser = (UserVO) request.getSession().getAttribute("loginUser");
            BoardDAO boardDAO = BoardDAO.getInstance();
            BoardVO board = boardDAO.selectBoardBySeq(boardSeq);

            // --- Authorization Check --- (isAdmin 파라미터 제거, 일반적인 방법으로는 수정 불가능)
            boolean isAuthor = loginUser != null && loginUser.getUserNm().equals(board.getBoardAuthor());
            if (!isAuthor) {
                NotificationUtil.addNotification(request, "게시글을 수정할 권한이 없습니다.", "error");
                response.sendRedirect(request.getContextPath() + "/playground/level3/list.do");
                return; // 권한이 없으면 여기서 처리 종료
            }

            Part filePart = request.getPart("file");

            // 0바이트 파일 업로드 체크
            if (filePart != null && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().trim().isEmpty() && filePart.getSize() == 0) {
                NotificationUtil.addNotification(request, "용량이 0인 파일은 업로드할 수 없습니다.", "error");
                response.sendRedirect(request.getContextPath() + "/playground/level3/view/" + boardSeq + ".do");
                return;
            }

            boolean newFileUploaded = (filePart != null && filePart.getSize() > 0);
            String originalFileName = null;
            String savedFileName = null;
            String uploadPath = request.getServletContext().getRealPath("/uploads");

            // 새 파일이 업로드된 경우, 유효성 검사를 먼저 수행합니다.
            if (newFileUploaded) {
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

                //level2 보다 강력한 파일 확장자 검증 (여전히 취약점은 존재해애함)
                List<String> allowedExtensions = Arrays.asList("txt", "pdf", "png", "jpg", "jpeg", "gif", "zip");
                if (extension.isEmpty() || !allowedExtensions.contains(extension)) {
                    // FeedbackUtil 대신 NotificationUtil 사용
                    NotificationUtil.addNotification(request, "허용되지 않는 파일 형식(" + extension + ")입니다.", "error");
                    response.sendRedirect(request.getContextPath() + "/playground/level3/view/" + boardSeq + ".do");
                    return;
                }
                
                // 의도된 취약점: UUID를 사용하지만, 파일명에 원본 파일명을 포함하여 Null Byte Injection에 취약하게 만듦
                savedFileName = UUID.randomUUID().toString() + "-" + originalFileName;
            }

            // 게시글 내용을 업데이트합니다.
            board.setBoardTitle(title);
            board.setBoardContents(content);
            boardDAO.updateBoard(board);

            FileDAO fileDAO = FileDAO.getInstance();
            
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 새 파일이 업로드되었거나, 기존 파일 삭제가 요청된 경우
            if (newFileUploaded || deleteExisting) {
                List<FileVO> existingFiles = fileDAO.getFilesByBoardSeq(boardSeq);
                for (FileVO fileVO : existingFiles) {
                    // 경로 상에 존재하는 파일 삭제
                    File fileToDelete = new File(uploadDir, fileVO.getSavedFileName());
                    if (fileToDelete.exists()) {
                        fileToDelete.delete();
                    }
                }
                fileDAO.deleteFilesByBoardSeq(boardSeq);
            }

            // 새 파일을 저장하고 DB에 기록합니다.
            if (newFileUploaded && savedFileName != null) {
                filePart.write(uploadPath + File.separator + savedFileName);
                fileDAO.insertFile(new FileVO(boardSeq, originalFileName, savedFileName));
            }

            NotificationUtil.addNotification(request, "게시글이 성공적으로 수정되었습니다.", "success");
            response.sendRedirect(request.getContextPath() + "/playground/level3/view/" + boardSeq + ".do");
        } catch (Exception e) {
            NotificationUtil.addNotification(request, "게시글 수정 중 오류가 발생했습니다: " + e.getMessage(), "error");
            response.sendRedirect(request.getContextPath() + "/playground/level3/list.do");
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