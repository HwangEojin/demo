package com.baseline.controller.playground.level3;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.baseline.controller.Action;
import com.baseline.dao.BoardDAO;
import com.baseline.dao.FileDAO;
import com.baseline.dto.BoardVO;
import com.baseline.util.NotificationUtil;
import com.baseline.util.FeedbackUtil;
import com.baseline.dto.FileVO;
import com.baseline.dto.UserVO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BoardDeleteActionLevel3 implements Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String boardSeqStr = (String) request.getAttribute("boardSeq");
        if (boardSeqStr == null) {
            boardSeqStr = request.getParameter("boardSeq");
        }

        if (boardSeqStr == null || boardSeqStr.isEmpty()) {
            NotificationUtil.addNotification(request, "잘못된 요청입니다. 게시글 번호가 없습니다.", "error");
            response.sendRedirect(request.getContextPath() + "/playground/level3/list.do");
            return;
        }

        try {
            int boardSeq = Integer.parseInt(boardSeqStr);

            UserVO loginUser = (UserVO) request.getSession().getAttribute("loginUser");
            BoardDAO boardDAO = BoardDAO.getInstance();
            BoardVO board = boardDAO.selectBoardBySeq(boardSeq);

            if (board == null) {
                NotificationUtil.addNotification(request, "삭제할 게시글이 존재하지 않습니다.", "error");
                response.sendRedirect(request.getContextPath() + "/playground/level3/list.do");
                return;
            }

            // --- Authorization Check --- 
            boolean isAuthor = loginUser != null && loginUser.getUserNm().equals(board.getBoardAuthor());
            if (!isAuthor) {
                NotificationUtil.addNotification(request, "게시글을 삭제할 권한이 없습니다.", "error");
                response.sendRedirect(request.getContextPath() + "/playground/level3/list.do");
                return; 
            }

            FileDAO fileDAO = FileDAO.getInstance();
            List<FileVO> fileList = fileDAO.getFilesByBoardSeq(boardSeq);

            String uploadPath = request.getServletContext().getRealPath("/uploads");
            File uploadDir = new File(uploadPath);

            for (FileVO fileVO : fileList) {
                File file = new File(uploadDir, fileVO.getSavedFileName());
                if (file.exists()) {
                    file.delete();
                }
            }

            // DB 첨부파일 레코드 및 게시글 삭제
            fileDAO.deleteFilesByBoardSeq(boardSeq);
            boardDAO.deleteBoardBySeq(boardSeq);

            NotificationUtil.addNotification(request, "게시글이 성공적으로 삭제되었습니다.", "success");
            response.sendRedirect(request.getContextPath() + "/playground/level3/list.do");

        } catch (Exception e) {
            NotificationUtil.addNotification(request, "게시글 삭제 중 오류가 발생했습니다: " + e.getMessage(), "error");
            response.sendRedirect(request.getContextPath() + "/playground/level3/list.do");
        }
    }
}