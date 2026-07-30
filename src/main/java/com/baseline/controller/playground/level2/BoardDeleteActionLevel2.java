package com.baseline.controller.playground.level2;

import java.io.File;
import java.io.IOException;
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

public class BoardDeleteActionLevel2 implements Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            //FrontController Attribute 및 Parameter 순차 검사
            String boardSeqStr = (String) request.getAttribute("boardSeq");
            if (boardSeqStr == null) {
                boardSeqStr = request.getParameter("boardSeq");
            }

            if (boardSeqStr == null || boardSeqStr.isEmpty()) {
                NotificationUtil.addNotification(request, "잘못된 요청입니다. 게시글 번호가 없습니다.", "error");
                response.sendRedirect(request.getContextPath() + "/playground/level2/list.do");
                return;
            }

            int boardSeq = Integer.parseInt(boardSeqStr);

           
            UserVO loginUser = (UserVO) request.getSession().getAttribute("loginUser");
            if (loginUser == null) {
                NotificationUtil.addNotification(request, "로그인이 필요합니다.", "error");
                response.sendRedirect(request.getContextPath() + "/user/login.do?targetUrl=" + request.getRequestURI());
                return;
            }

            // 3. 게시글 존재 여부 확인
            BoardDAO boardDAO = BoardDAO.getInstance();
            BoardVO board = boardDAO.selectBoardBySeq(boardSeq);

            if (board == null) {
                NotificationUtil.addNotification(request, "삭제할 게시글이 존재하지 않습니다.", "error");
                response.sendRedirect(request.getContextPath() + "/playground/level2/list.do");
                return;
            }

            // 작성자 본인 확인(Authorization Check)을 하지 않고 삭제 흐름을 계속 진행함
            boolean isAuthor = loginUser.getUserNm().equals(board.getBoardAuthor());

            // 작성자가 아닌 타인이 삭제를 시도하여 성공한 경우 IDOR 취약점 피드백 세션 저장
            if (!isAuthor) {
                FeedbackUtil.setFeedback(
                    request,
                    "IDOR",
                    "IDOR 공격 성공",
                    "본인이 작성하지 않은 게시글의 식별자(boardSeq)를 변조하여 임의 삭제에 성공했습니다.", 
                    "공격 주체: " + loginUser.getUserNm() + "\n삭제 타겟 글 번호: " + boardSeq
                );
            }

            // 5. 첨부파일 파일 시스템 삭제
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

            fileDAO.deleteFilesByBoardSeq(boardSeq);
            boardDAO.deleteBoardBySeq(boardSeq);

            NotificationUtil.addNotification(request, "게시글이 성공적으로 삭제되었습니다.", "success");
            response.sendRedirect(request.getContextPath() + "/playground/level2/list.do");

        } catch (Exception e) {
            NotificationUtil.addNotification(request, "게시글 삭제 중 오류가 발생했습니다: " + e.getMessage(), "error");
            response.sendRedirect(request.getContextPath() + "/playground/level2/list.do");
        }
    }
}