package com.baseline.controller.admin;

import java.io.IOException;
import java.util.List;

import com.baseline.controller.Action;
import com.baseline.dao.UserDAO;
import com.baseline.dto.UserVO;
import com.baseline.dto.Pagination;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class UserManagementAction implements Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 필터에서 이미 관리자 여부를 확인하지만, 2차 방어 로직을 추가합니다.
        UserVO loginUser = (UserVO) request.getSession().getAttribute("loginUser");
        if (loginUser == null || !"A".equals(loginUser.getUserTp())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
            return;
        }

        UserDAO userDAO = UserDAO.getInstance();
        
        String searchType = request.getParameter("searchType");
        String keyword = request.getParameter("keyword");

        String pageStr = request.getParameter("page");
        int page = (pageStr == null || pageStr.isEmpty()) ? 1 : Integer.parseInt(pageStr);
        int pageSize = 10; // 한 페이지에 10명씩

        int totalCount = userDAO.getUserCount(searchType, keyword);
        Pagination pagination = new Pagination(page, pageSize, totalCount);

        List<UserVO> userList = userDAO.selectAllUsers(page, pageSize, searchType, keyword);
        request.setAttribute("userList", userList);
        request.setAttribute("pagination", pagination);
        request.setAttribute("searchType", searchType);
        request.setAttribute("keyword", keyword);
        request.getRequestDispatcher("/admin/user_manage.jsp").forward(request, response);
    }
}