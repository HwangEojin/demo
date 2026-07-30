package com.baseline.controller.playground.level1;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

public class BoardDeleteActionLevel1 implements Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int boardSeq = Integer.parseInt(request.getParameter("boardSeq"));

            BoardDAO boardDAO = BoardDAO.getInstance();
            BoardVO board = boardDAO.selectBoardBySeq(boardSeq);

            if (board == null) {
                NotificationUtil.addNotification(request, "삭제할 게시글이 존재하지 않습니다.", "error");
                response.sendRedirect(request.getContextPath() + "/playground/level1/list.do");
                return;
            }
            
            UserVO loginUser = (UserVO) request.getSession().getAttribute("loginUser");
            
            //작성자가 아닌 경우 모두 IDOR 공격 성공으로 처리
            boolean isUnauthorized = (loginUser == null) || (!loginUser.getUserNm().equals(board.getBoardAuthor()));
            
            if (isUnauthorized) {
                String attacker = (loginUser != null) ? loginUser.getUserNm() : "비인가 사용자(Guest)";
                FeedbackUtil.setFeedback(request, "IDOR", "IDOR 공격 성공",
                    "본인이 작성하지 않은 게시글의 식별자(boardSeq)를 변조하여 임의 삭제에 성공했습니다.", 
                    "공격 주체: " + attacker + "\n삭제 타겟 글 번호: " + boardSeq);
            }

            FileDAO fileDAO = FileDAO.getInstance();
            List<FileVO> fileList = fileDAO.getFilesByBoardSeq(boardSeq); 
            String uploadPath = request.getServletContext().getRealPath("/uploads");

            for (FileVO fileVO : fileList) {
                File file = new File(uploadPath, fileVO.getSavedFileName());
                if (file.exists()) file.delete();
            }
            
            fileDAO.deleteFilesByBoardSeq(boardSeq); 
            boardDAO.deleteBoardBySeq(boardSeq); 

            // 삭제 성공 알림 추가
            NotificationUtil.addNotification(request, "게시글이 성공적으로 삭제되었습니다.", "success");

            // Redirect 후 클라이언트에서 pollFeedbackStatus()가 실행되며 모달 노출
            response.sendRedirect(request.getContextPath() + "/playground/level1/list.do");

        } catch (Exception e) {
            NotificationUtil.addNotification(request, "게시글 삭제 중 오류가 발생했습니다: " + e.getMessage(), "error");
            response.sendRedirect(request.getContextPath() + "/playground/level1/list.do");
        }
    }
}