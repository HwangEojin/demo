package com.baseline.controller.admin;

import java.io.IOException;

import com.baseline.controller.Action;
import com.baseline.dao.UserDAO;
import com.baseline.dto.UserVO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class UserDeleteAction implements Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserVO loginUser = (UserVO) request.getSession().getAttribute("loginUser");
        if (loginUser == null || !"A".equals(loginUser.getUserTp())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
            return;
        }

        int userSeq = Integer.parseInt(request.getParameter("userSeq"));
        String page = request.getParameter("page");

        // 관리자가 자신의 계정을 삭제하는 것을 방지
        if (loginUser.getUserSeq() == userSeq) {
             response.setContentType("text/html; charset=UTF-8");
             java.io.PrintWriter out = response.getWriter();
             out.println("<script>alert('자신의 계정은 삭제할 수 없습니다.'); location.href='" + request.getContextPath() + "/admin/user_manage.do?page=" + page + "';</script>");
             out.flush();
             return;
        }

        UserDAO.getInstance().deleteUser(userSeq);

        response.sendRedirect(request.getContextPath() + "/admin/user_manage.do?page=" + page);
    }
}