package com.baseline.controller;

import java.util.HashMap;
import java.util.Map;
import com.baseline.controller.api.FeedbackAction;

import com.baseline.controller.api.UserWithdrawalAction;
import com.baseline.controller.api.UserVerifyPasswordAction;
import com.baseline.controller.api.FeedbackStatusAction;
import com.baseline.controller.playground.PlaygroundMainAction;
import com.baseline.controller.playground.level1.FileDownloadActionLevel1;
import com.baseline.controller.playground.level1.BoardDeleteActionLevel1;
import com.baseline.controller.playground.level1.BoardListActionLevel1;
import com.baseline.controller.playground.level1.BoardUpdateActionLevel1;
import com.baseline.controller.playground.level1.BoardUpdateFormActionLevel1;
import com.baseline.controller.playground.level1.BoardViewActionLevel1;
import com.baseline.controller.playground.level1.BoardWriteActionLevel1;
import com.baseline.controller.playground.level1.BoardWriteFormActionLevel1;
import com.baseline.controller.playground.level2.BoardDeleteActionLevel2;
import com.baseline.controller.playground.level2.BoardListActionLevel2;
import com.baseline.controller.playground.level2.BoardUpdateActionLevel2;
import com.baseline.controller.playground.level2.BoardUpdateFormActionLevel2;
import com.baseline.controller.playground.level2.BoardViewActionLevel2;
import com.baseline.controller.playground.level2.BoardWriteActionLevel2;
import com.baseline.controller.playground.level2.BoardWriteFormActionLevel2;
import com.baseline.controller.playground.level2.FileDownloadActionLevel2;
import com.baseline.controller.playground.level3.BoardDeleteActionLevel3;
import com.baseline.controller.playground.level3.BoardListActionLevel3;
import com.baseline.controller.playground.level3.BoardUpdateActionLevel3;
import com.baseline.controller.playground.level3.BoardUpdateFormActionLevel3;
import com.baseline.controller.playground.level3.BoardViewActionLevel3;
import com.baseline.controller.playground.level3.BoardWriteActionLevel3;
import com.baseline.controller.playground.level3.BoardWriteFormActionLevel3;
import com.baseline.controller.playground.level4.BoardListActionLevel4;
import com.baseline.controller.playground.level4.BoardViewActionLevel4;
import com.baseline.controller.playground.level4.BoardWriteActionLevel4;
import com.baseline.controller.playground.level4.BoardWriteFormActionLevel4;
import com.baseline.controller.playground.level3.FileDownloadActionLevel3;
import com.baseline.controller.admin.UserAuthUpdateAction;
import com.baseline.controller.admin.UserDeleteAction;
import com.baseline.controller.admin.UserManagementAction;
import com.baseline.controller.user.MyPageFormAction;
import com.baseline.controller.user.LoginAction;
import com.baseline.controller.user.LoginFormAction;
import com.baseline.controller.user.LogoutAction;
import com.baseline.controller.user.SignupAction;
import com.baseline.controller.user.SignupFormAction;
import com.baseline.controller.user.UserUpdateAction;

public class ActionFactory {
    private static ActionFactory instance = new ActionFactory();
    private final Map<String, Action> actionMap = new HashMap<>();

    private ActionFactory() {
        super();
        actionMap.put("login", new LoginAction());
        actionMap.put("login_form", new LoginFormAction());
        actionMap.put("logout", new LogoutAction());
        actionMap.put("signup_form", new SignupFormAction());
        actionMap.put("signup", new SignupAction());
        actionMap.put("mypage_form", new MyPageFormAction());
        actionMap.put("user_update", new UserUpdateAction());

        // 플레이그라운드 메인
        actionMap.put("playground_main", new PlaygroundMainAction());

        // API
        actionMap.put("api_feedback", new FeedbackAction());
        actionMap.put("api_feedback_status", new FeedbackStatusAction());
        actionMap.put("api_verify_password", new UserVerifyPasswordAction());
        actionMap.put("api_user_withdrawal", new UserWithdrawalAction());

        // 보안 수준 1(vulnerable) 게시판 액션
        actionMap.put("playground_level1_list", new BoardListActionLevel1());
        actionMap.put("playground_level1_view", new BoardViewActionLevel1());
        actionMap.put("playground_level1_write_form", new BoardWriteFormActionLevel1());
        actionMap.put("playground_level1_write", new BoardWriteActionLevel1());
        actionMap.put("playground_level1_update_form", new BoardUpdateFormActionLevel1());
        actionMap.put("playground_level1_update", new BoardUpdateActionLevel1());
        actionMap.put("playground_level1_delete", new BoardDeleteActionLevel1());
        actionMap.put("playground_level1_download", new FileDownloadActionLevel1());

        // 보안 수준 2(intermediate) 게시판 액션
        actionMap.put("playground_level2_list", new BoardListActionLevel2());
        actionMap.put("playground_level2_view", new BoardViewActionLevel2());
        actionMap.put("playground_level2_write_form", new BoardWriteFormActionLevel2());
        actionMap.put("playground_level2_write", new BoardWriteActionLevel2());
        actionMap.put("playground_level2_update_form", new BoardUpdateFormActionLevel2());
        actionMap.put("playground_level2_update", new BoardUpdateActionLevel2());
        actionMap.put("playground_level2_delete", new BoardDeleteActionLevel2());
        actionMap.put("playground_level2_download", new FileDownloadActionLevel2());

        // 보안 수준 3(secure) 게시판 액션
        actionMap.put("playground_level3_list", new BoardListActionLevel3());
        actionMap.put("playground_level3_view", new BoardViewActionLevel3());
        actionMap.put("playground_level3_write_form", new BoardWriteFormActionLevel3());
        actionMap.put("playground_level3_write", new BoardWriteActionLevel3());
        actionMap.put("playground_level3_update_form", new BoardUpdateFormActionLevel3());
        actionMap.put("playground_level3_update", new BoardUpdateActionLevel3());
        actionMap.put("playground_level3_delete", new BoardDeleteActionLevel3());

        // 보안 수준 4(LFI) 게시판 액션
        actionMap.put("playground_level4_list", new BoardListActionLevel4());
        actionMap.put("playground_level4_view", new BoardViewActionLevel4());
        actionMap.put("playground_level4_write_form", new BoardWriteFormActionLevel4());
        actionMap.put("playground_level4_write", new BoardWriteActionLevel4());

        actionMap.put("user_manage", new UserManagementAction());
        actionMap.put("update_auth", new UserAuthUpdateAction());
        actionMap.put("delete_user", new UserDeleteAction());
        actionMap.put("file_download", new FileDownloadActionLevel3());
    }

    public static ActionFactory getInstance() {
        return instance;
    }

    public Action getAction(String command) {
        System.out.println("ActionFactory : " + command);
        return actionMap.get(command);
    }
}