package com.baseline.controller.playground.level4;

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

public class BoardWriteActionLevel4 implements Action {

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

            BoardVO boardVO = new BoardVO();
            boardVO.setBoardTitle(title);
            boardVO.setBoardContents(content);
            boardVO.setBoardAuthor(loginUser.getUserNm());
            boardVO.setBoardLevel(4);

            BoardDAO boardDAO = BoardDAO.getInstance();
            int boardSeq = boardDAO.insertBoard(boardVO);

            Part filePart = request.getPart("file");
            if (filePart != null && filePart.getSize() > 0) {
                String originalFileName = new File(filePart.getSubmittedFileName()).getName();
                String extension = "";
                int dotIndex = originalFileName.lastIndexOf('.');

                if (dotIndex > 0 && dotIndex < originalFileName.length() - 1) {
                    extension = originalFileName.substring(dotIndex + 1).toLowerCase();
                }

                List<String> allowedExtensions = Arrays.asList("txt", "pdf", "png", "jpg", "jpeg", "gif", "zip");
                if (extension.isEmpty() || !allowedExtensions.contains(extension)) {
                    // FeedbackUtil 대신 NotificationUtil 사용
                    NotificationUtil.addNotification(request, "허용되지 않는 파일 형식(" + extension + ")입니다.", "error");
                    response.sendRedirect(request.getContextPath() + "/playground/level4/list.do");
                    return;
                }

                String savedFileName = UUID.randomUUID().toString() + "." + extension;
                String uploadPath = request.getServletContext().getRealPath("/uploads");
                
                new File(uploadPath).mkdirs();
                filePart.write(uploadPath + File.separator + savedFileName);

                FileDAO.getInstance().insertFile(new FileVO(boardSeq, originalFileName, savedFileName));
            }

            NotificationUtil.addNotification(request, "게시글이 성공적으로 등록되었습니다.", "success");
            response.sendRedirect(request.getContextPath() + "/playground/level4/view/" + boardSeq + ".do");
        } catch (Exception e) {
            NotificationUtil.addNotification(request, "게시글 작성 중 오류가 발생했습니다: " + e.getMessage(), "error");
            response.sendRedirect(request.getContextPath() + "/playground/level4/write.do");
        }
    }
}