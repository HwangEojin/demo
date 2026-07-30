package com.baseline.controller.playground.level3;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Arrays;

import com.baseline.controller.Action;
import com.baseline.dao.BoardDAO;
import com.baseline.dto.BoardVO;
import com.baseline.dto.Pagination;
import com.baseline.util.NotificationUtil;
import com.baseline.util.FeedbackUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BoardListActionLevel3 implements Action {

    // Level 3에서는 오탐을 줄이기 위해 실제 공격 구문에 가까운 키워드만 탐지
    private static final List<String> SQLI_KEYWORDS_LEVEL3 = Arrays.asList(
            "sleep(", "benchmark(", "waitfor delay",
            "if(", "case when",
            "substring(", "substr(", "ord(", "char(", "concat(", "length(",
            "extractvalue(", "updatexml(", 
            "load_file(", "outfile", "into dumpfile", "xp_cmdshell", "exec(",
            "union select", "union all select"
            // '--', ';', '/*', 'or 1=1' 등은 PreparedStatement 환경에서는 오탐 가능성이 높아 제외
    );

    private static final Pattern[] XSS_PATTERNS = new Pattern[] {
        Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("src[\r\n]*=[\r\n]*['\"](.*?)['\"]", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("on(blur|change|click|dblclick|error|focus|keydown|keypress|keyup|load|mousedown|mousemove|mouseout|mouseover|mouseup|reset|select|submit|unload|toggle)=", Pattern.CASE_INSENSITIVE)
    };

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchType = request.getParameter("searchType");
        String originalKeyword = request.getParameter("keyword");
        String sanitizedKeyword = sanitize(originalKeyword);

        boolean isXssAttempt = checkXssPayload(originalKeyword);
        // XSS 공격이 아닐 경우에만 SQLi 시도를 검사하여 중복 탐지를 방지합니다.
        boolean isSqliAttempt = !isXssAttempt && isSqlInjectionAttempt(originalKeyword);

        // Reflected XSS 공격 시도 탐지: 필터링을 우회하여 악성 스크립트가 남아있는 경우
        if (isXssAttempt && checkXssPayload(sanitizedKeyword) && request.getSession().getAttribute("feedback_type") == null) {
            FeedbackUtil.setFeedback(request, "XSS", "XSS 필터 우회 성공",
                    "필터링을 우회하여 반사(Reflected) XSS 공격에 성공했습니다.", originalKeyword);
        }

        try {
            BoardDAO boardDAO = BoardDAO.getInstance();
    
            String pageStr = request.getParameter("page");
            int page = (pageStr == null || pageStr.isEmpty()) ? 1 : Integer.parseInt(pageStr);
            int pageSize = 10; // 한 페이지에 10개씩
            
            // DAO는 PreparedStatement를 사용하므로 SQLi 공격은 방어됩니다.
            int totalCount = boardDAO.getBoardCountLevel3(searchType, sanitizedKeyword);
            Pagination pagination = new Pagination(page, pageSize, totalCount);
            
            List<BoardVO> boardList = boardDAO.selectAllBoardsLevel3(page, pageSize, searchType, sanitizedKeyword);
            
            // 게시글 목록 자체에 포함된 XSS 페이로드 검사 (다른 피드백이 없을 때만)
            if (request.getSession().getAttribute("feedback_type") == null) {
                if (boardList != null) {
                    for (BoardVO board : boardList) {
                        if (checkXssPayload(board.getBoardTitle())) {
                            FeedbackUtil.setFeedback(request, "XSS", "저장된 XSS 공격 성공",
                                    "게시글 제목에 XSS 페이로드가 성공적으로 주입되었습니다.", board.getBoardTitle());
                            break; // 첫 번째 페이로드 발견 시 중단
                        }
                        if (checkXssPayload(board.getBoardContents())) {
                            FeedbackUtil.setFeedback(request, "XSS", "저장된 XSS 공격 성공",
                                    "게시글 내용에 XSS 페이로드가 성공적으로 주입되었습니다.", board.getBoardContents());
                            break; // 첫 번째 페이로드 발견 시 중단
                        }
                    }
                }
            }
            
            request.setAttribute("boardList", boardList);
            request.setAttribute("pagination", pagination);
            request.setAttribute("searchType", searchType);
            request.setAttribute("keyword", originalKeyword); // 화면에는 필터링 전 원본 키워드 표시
            
            request.getRequestDispatcher("/playground/level3/list.jsp").forward(request, response);
        } catch (Exception e) {
            // Error-Based SQLi 성공 시 피드백 (Level 3에서는 PreparedStatement를 사용하므로 이론적으로 발생하기 어렵습니다)
            boolean isSqlError = false;
            String sqlErrorMessage = "";
            Throwable cause = e;
            while (cause != null) {
                if (cause instanceof java.sql.SQLException) {
                    isSqlError = true;
                    sqlErrorMessage = cause.getMessage();
                    break;
                }
                cause = cause.getCause();
            }

            if (isSqliAttempt && isSqlError) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                FeedbackUtil.setFeedback(request, "SQLI", "Error-Based SQL Injection 성공",
                        "의도적으로 유발된 데이터베이스 에러 메시지를 통해 내부 정보가 노출되었습니다.",
                        "사용한 페이로드: " + originalKeyword + "\n\nDB 에러: " + sqlErrorMessage);
            } else if (!isSqliAttempt) { 
                // SQLi 시도가 아닌 다른 예외의 경우에만 일반 오류 알림
                NotificationUtil.addNotification(request, "게시글 목록을 불러오는 중 오류가 발생했습니다: " + e.getMessage(), "error");
            }
            // 피드백 또는 알림을 표시하기 위해 페이지 리다이렉트
            response.sendRedirect(request.getContextPath() + "/playground/level3/list.do");
        }
    }

    private boolean isSqlInjectionAttempt(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return false;
        String lowerKeyword = keyword.toLowerCase();
        for (String pattern : SQLI_KEYWORDS_LEVEL3) {
            if (lowerKeyword.contains(pattern)) return true;
        }
        return false;
    }

    private boolean checkXssPayload(String value) {
        if (value == null || value.trim().isEmpty()) return false;
        for (Pattern pattern : XSS_PATTERNS) {
            if (pattern.matcher(value).find()) return true;
        }
        return false;
    }

    private String sanitize(String keyword) {
        if (keyword == null) {
            return null;
        }
        // 대소문자를 구분하지 않고 일부 키워드를 필터링하지만, 중첩이나 인코딩 우회에는 취약
        String sanitized = keyword.replaceAll("(?i)<script>", "");
        sanitized = sanitized.replaceAll("(?i)</script>", ""); // XSS
        sanitized = sanitized.replaceAll("(?i)alert", ""); // XSS
        sanitized = sanitized.replaceAll("(?i)eval", ""); // XSS
        sanitized = sanitized.replaceAll("(?i)javascript:", ""); // XSS
        sanitized = sanitized.replaceAll("(?i)onload", ""); // XSS
        sanitized = sanitized.replaceAll("(?i)onerror", ""); // XSS
        sanitized = sanitized.replaceAll("(?i)union", ""); // SQLi
        sanitized = sanitized.replaceAll("(?i)select", ""); // SQLi
        sanitized = sanitized.replaceAll("(?i)and", ""); // SQLi
        sanitized = sanitized.replaceAll("(?i)or", ""); // SQLi
        sanitized = sanitized.replaceAll("(?i)sleep", ""); // SQLi
        sanitized = sanitized.replaceAll("(?i)benchmark", ""); // SQLi
        return sanitized;
    }

}