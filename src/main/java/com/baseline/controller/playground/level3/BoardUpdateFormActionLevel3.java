package com.baseline.controller.playground.level3;

import java.io.IOException;
import java.util.List;

import com.baseline.controller.Action;
import com.baseline.dao.BoardDAO;
import com.baseline.dao.FileDAO;
import com.baseline.dto.BoardVO;
import com.baseline.util.NotificationUtil;
import com.baseline.dto.FileVO;
import com.baseline.dto.UserVO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BoardUpdateFormActionLevel3 implements Action {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String boardSeqStr = (String) request.getAttribute("boardSeq");
            int boardSeq = Integer.parseInt(boardSeqStr);

            BoardDAO boardDAO = BoardDAO.getInstance();
            BoardVO board = boardDAO.selectBoardBySeq(boardSeq);
            
            FileDAO fileDAO = FileDAO.getInstance();
            List<FileVO> fileList = fileDAO.getFilesByBoardSeq(boardSeq);

            UserVO loginUser = (UserVO) request.getSession().getAttribute("loginUser");

            request.setAttribute("board", board);
            request.setAttribute("fileList", fileList);
            request.getRequestDispatcher("/playground/level3/update.jsp").forward(request, response);
        } catch (Exception e) {
            NotificationUtil.addNotification(request, "게시글 수정 양식을 불러오는 중 오류가 발생했습니다: " + e.getMessage(), "error");
            response.sendRedirect(request.getContextPath() + "/playground/level3/list.do");
        }
    }
}