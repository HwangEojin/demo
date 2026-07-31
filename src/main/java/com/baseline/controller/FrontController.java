package com.baseline.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("*.do")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,    // 1MB
    maxFileSize = 1024 * 1024 * 10,     // 10MB
    maxRequestSize = 1024 * 1024 * 50   // 50MB
)
public class FrontController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String commandPath = requestURI.substring(contextPath.length());

        System.out.println("--- FrontController ---");
        System.out.println("Request URI: " + requestURI);
        System.out.println("Context Path: " + contextPath);
        System.out.println("Command Path: " + commandPath);

        ActionFactory af = ActionFactory.getInstance();
        Action action = null;
        String command = null;

        // .do 확장자 제거 및 경로 분리
        String path = commandPath.substring(0, commandPath.length() - 3); // e.g., /user/login
        String[] pathParts = path.split("/"); // e.g., ["", "user", "login"]

        if (pathParts.length > 1) {
            String module = pathParts[1];

            switch (module) {
                case "user":
                    if (pathParts.length > 2) {
                        String mainAction = pathParts[2];
                        switch (mainAction) {
                            case "login":
                                command = request.getMethod().equalsIgnoreCase("POST") ? "login" : "login_form";
                                break;
                            case "signup":
                                command = request.getMethod().equalsIgnoreCase("POST") ? "signup" : "signup_form";
                                break;
                            case "logout":
                                command = "logout";
                                break;
                            case "mypage":
                                command = "mypage_form";
                                break;
                            case "update":
                                command = "user_update";
                                break;
                        }
                    } else {
                        response.sendRedirect(contextPath + "/user/login.do");
                        return;
                    }
                    break;

                case "playground":
                    if (pathParts.length > 2) {
                        String level = pathParts[2]; // e.g., "level3", "vulnerable"
                        if ("main".equals(level)) {
                            command = "playground_main";
                            break;
                        }
                        if ("level1".equals(level) && pathParts.length > 3) {
                            String mainAction = pathParts[3]; // e.g., "list", "view"
                            switch (mainAction) {
                                case "list":
                                    command = "playground_level1_list";
                                    break;
                                case "view":
                                    if (pathParts.length > 4) {
                                        command = "playground_level1_view";
                                        request.setAttribute("boardSeq", pathParts[4]);
                                    }
                                    break;
                                case "write":
                                    command = request.getMethod().equalsIgnoreCase("POST") ? "playground_level1_write" : "playground_level1_write_form";
                                    break;
                                case "update":
                                    if (request.getMethod().equalsIgnoreCase("POST")) {
                                        command = "playground_level1_update";
                                    } else { // GET
                                        if (pathParts.length > 4) {
                                            command = "playground_level1_update_form";
                                            request.setAttribute("boardSeq", pathParts[4]);
                                        }
                                    }
                                    break;
                                case "delete":
                                    command = "playground_level1_delete";
                                    break;
                                case "download":
                                    command = "playground_level1_download";
                                    break;
                            }
                        } else if ("level2".equals(level) && pathParts.length > 3) {
                            String mainAction = pathParts[3]; // e.g., "list", "view"
                            switch (mainAction) {
                                case "list":
                                    command = "playground_level2_list";
                                    break;
                                case "view":
                                    if (pathParts.length > 4) {
                                        command = "playground_level2_view";
                                        request.setAttribute("boardSeq", pathParts[4]);
                                    }
                                    break;
                                case "write":
                                    command = request.getMethod().equalsIgnoreCase("POST") ? "playground_level2_write" : "playground_level2_write_form";
                                    break;
                                case "update":
                                    if (request.getMethod().equalsIgnoreCase("POST")) {
                                        command = "playground_level2_update";
                                    } else { // GET
                                        if (pathParts.length > 4) {
                                            command = "playground_level2_update_form";
                                            request.setAttribute("boardSeq", pathParts[4]);
                                        }
                                    }
                                    break;
                                case "delete":
                                     if (pathParts.length > 4) {
                                        command = "playground_level2_delete";
                                        request.setAttribute("boardSeq", pathParts[4]);
                                    }
                                    break;
                                case "download":
                                    command = "playground_level2_download";
                                    break;
                            }
                        } else if ("level3".equals(level) && pathParts.length > 3) {
                            String mainAction = pathParts[3]; // e.g., "list", "view"
                            switch (mainAction) {
                                case "list":
                                    command = "playground_level3_list";
                                    break;
                                case "view":
                                    if (pathParts.length > 4) {
                                        command = "playground_level3_view";
                                        request.setAttribute("boardSeq", pathParts[4]);
                                    }
                                    break;
                                case "write":
                                    command = request.getMethod().equalsIgnoreCase("POST") ? "playground_level3_write" : "playground_level3_write_form";
                                    break;
                                case "update":
                                    if (request.getMethod().equalsIgnoreCase("POST")) {
                                        command = "playground_level3_update";
                                        // boardSeq는 암호화된 폼 파라미터로 전달됩니다.
                                    } else { // GET
                                        if (pathParts.length > 4) {
                                            command = "playground_level3_update_form";
                                            request.setAttribute("boardSeq", pathParts[4]);
                                        }
                                    }
                                    break;
                                case "delete":
                                     if (pathParts.length > 4) {
                                        command = "playground_level3_delete";
                                        request.setAttribute("boardSeq", pathParts[4]);
                                    }
                                    break;
                            }
                        } else if ("level4".equals(level) && pathParts.length > 3) {
                            String mainAction = pathParts[3]; // e.g., "list", "view"
                            switch (mainAction) {
                                case "list":
                                    command = "playground_level4_list";
                                    break;
                                case "view":
                                    if (pathParts.length > 4) {
                                        command = "playground_level4_view";
                                        request.setAttribute("boardSeq", pathParts[4]);
                                    }
                                    break;
                                case "write":
                                    command = "playground_level4_write_form";
                                    break;
                                case "writeProc":
                                    command = "playground_level4_write";
                                    break;
                            }
                        }
                    } else {
                        // /playground/ 또는 /playground.do 로 접근 시 메인 페이지로 이동
                        command = "playground_main";
                    }
                    break;

                case "api":
                    if (pathParts.length > 2) {
                        String mainAction = pathParts[2]; // e.g., "feedback"
                        switch (mainAction) {
                            case "feedback":
                                if (pathParts.length > 3 && "status".equals(pathParts[3])) {
                                    command = "api_feedback_status";
                                } else {
                                    command = "api_feedback";
                                }
                                break;
                            case "verifyPassword":
                                command = "api_verify_password";
                                break;
                            case "withdrawal":
                                command = "api_user_withdrawal";
                                break;
                        }
                    }
                    break;

                case "admin":
                    if (pathParts.length > 2) {
                        command = pathParts[2]; // e.g., "user_manage"
                    } else {
                        response.sendRedirect(contextPath + "/admin/user_manage.do");
                        return;
                    }
                    break;
                
                case "file":
                    if (pathParts.length > 2) {
                        String mainAction = pathParts[2];
                        switch(mainAction) {
                            case "download":
                                if (pathParts.length > 3) {
                                    command = "file_download";
                                    request.setAttribute("fileSeq", pathParts[3]);
                                }
                                break;
                        }
                    }
            }
        } else {
            response.sendRedirect(contextPath + "/user/login.do");
            return;
        }

        if (command != null) {
            System.out.println("Executing command: " + command);
            action = af.getAction(command);
        }

        if (action != null) {
            action.execute(request, response);
        } else {
            System.out.println("No action found for command: " + command + ". Sending 404.");
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}