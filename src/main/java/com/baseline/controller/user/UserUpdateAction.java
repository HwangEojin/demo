package com.baseline.controller.user;

import java.io.IOException;

import com.baseline.controller.Action;
import com.baseline.dao.UserDAO;
import com.baseline.dto.UserVO;
import com.baseline.util.NotificationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class UserUpdateAction implements Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserVO loginUser = (UserVO) session.getAttribute("loginUser");

        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.do");
            return;
        }

        // 폼에서 업데이트된 데이터 가져오기
        String userPw = request.getParameter("userPw");
        String userNm = request.getParameter("userNm");
        String userEmail = request.getParameter("userEmail");
        String userNum = request.getParameter("userNum");

        // 업데이트할 사용자 정보 설정
        UserVO updatedUser = new UserVO();
        updatedUser.setUserSeq(loginUser.getUserSeq());
        updatedUser.setUserNm(userNm);
        updatedUser.setUserEmail(userEmail);
        updatedUser.setUserNum(userNum);

        // 새 비밀번호가 입력된 경우에만 비밀번호 업데이트
        if (userPw != null && !userPw.trim().isEmpty()) {
            updatedUser.setUserPw(userPw);
        } else {
            updatedUser.setUserPw(loginUser.getUserPw()); // 기존 비밀번호 유지
        }

        UserDAO userDAO = UserDAO.getInstance();
        userDAO.updateUser(updatedUser);

        // 세션에 저장된 사용자 정보 업데이트
        UserVO refreshedUser = userDAO.selectUserBySeq(loginUser.getUserSeq());
        session.setAttribute("loginUser", refreshedUser);

        NotificationUtil.addNotification(request, "회원 정보가 성공적으로 수정되었습니다.", "success");
        response.sendRedirect(request.getContextPath() + "/user/mypage.do");
    }
}