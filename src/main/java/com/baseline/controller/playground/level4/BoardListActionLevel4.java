package com.baseline.controller.playground.level4;

import java.io.IOException;
import java.util.List;

import com.baseline.controller.Action;
import com.baseline.dao.BoardDAO;
import com.baseline.dto.BoardVO;
import com.baseline.dto.Pagination;
import com.baseline.util.NotificationUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BoardListActionLevel4 implements Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            BoardDAO boardDAO = BoardDAO.getInstance();

            String pageStr = request.getParameter("page");
            int page = (pageStr == null || pageStr.isEmpty()) ? 1 : Integer.parseInt(pageStr);
            int pageSize = 10;

            int totalCount = boardDAO.getBoardCountByLevel(4); 
            Pagination pagination = new Pagination(page, pageSize, totalCount);

            List<BoardVO> boardList = boardDAO.selectAllBoardsByLevel(4, page, pageSize);

            request.setAttribute("boardList", boardList);
            request.setAttribute("pagination", pagination);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/playground/level4/list.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            NotificationUtil.addNotification(request, "게시글 목록을 불러오는 중 오류가 발생했습니다: " + e.getMessage(), "error");
            response.sendRedirect(request.getContextPath() + "/playground/main.do");
        }
    }
}