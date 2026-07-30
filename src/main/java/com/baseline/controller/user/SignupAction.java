package com.baseline.controller.user;

import java.io.IOException;

import com.baseline.controller.Action;
import com.baseline.dao.UserDAO;
import com.baseline.dto.UserVO;
import com.baseline.util.NotificationUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SignupAction implements Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getParameter("userId");
        String userPw = request.getParameter("userPw");
        String userNm = request.getParameter("userNm");
        String userNum = request.getParameter("userNum");
        String userEmail = request.getParameter("userEmail");

        if (userId == null || userId.trim().isEmpty() ||
            userPw == null || userPw.trim().isEmpty() ||
            userNm == null || userNm.trim().isEmpty()) {
            NotificationUtil.addNotification(request, "필수 항목(아이디, 비밀번호, 이름)을 모두 입력해주세요.", "error");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/user/signup.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // 비밀번호 정책 검사 (8자리 이상, 영문, 숫자, 특수문자 포함)
        String pwPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
        if (!userPw.matches(pwPattern)) {
            NotificationUtil.addNotification(request, "비밀번호는 8자리 이상이며, 영문, 숫자, 특수문자를 모두 포함해야 합니다.", "error");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/user/signup.jsp");
            dispatcher.forward(request, response);
            return;
        }

        UserDAO userDAO = UserDAO.getInstance();

        if (userDAO.isUserIdExists(userId)) {
            NotificationUtil.addNotification(request, "이미 사용 중인 아이디입니다. 다른 아이디를 입력해주세요.", "error");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/user/signup.jsp");
            dispatcher.forward(request, response);
            return;
        }

        UserVO userVO = new UserVO();
        userVO.setUserId(userId);
        userVO.setUserPw(userPw);
        userVO.setUserNm(userNm);
        userVO.setUserNum(userNum);
        userVO.setUserEmail(userEmail);
        int result = userDAO.signup(userVO);

        if (result > 0) {
            NotificationUtil.addNotification(request, "회원가입이 완료되었습니다. 로그인해주세요.", "success");
            response.sendRedirect(request.getContextPath() + "/user/login.do");
        } else {
            NotificationUtil.addNotification(request, "회원가입에 실패했습니다. 다시 시도해주세요.", "error");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/user/signup.jsp");
            dispatcher.forward(request, response);
        }
    }
}