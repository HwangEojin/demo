package com.baseline.controller.playground.level2;

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
import com.baseline.util.FeedbackUtil;
import com.baseline.util.NotificationUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BoardListActionLevel2 implements Action {

    // Level 1과 동일한 탐지 패턴 사용
    private static final List<String> COMPREHENSIVE_SQLI_KEYWORDS = Arrays.asList(
            "sleep(", "benchmark(", "waitfor delay",
            "if(", "case when", "and 1=1", "and 1=2", "or 1=1", "or 1=2",
            "substring(", "substr(", "ord(", "char(", "concat(", "length(",
            "extractvalue(", "updatexml(",
            "load_file(", "outfile", "into dumpfile", "xp_cmdshell", "exec(",
            "union select", "union all select",
            "--", "#", "/*", "*/", ";", "%00");

    private static final List<String> DB_SCHEMA_KEYWORDS = Arrays.asList(
            "tb_user", "tb_auth", "user_seq", "user_id",
            "user_pw", "user_nm", "user_num", "user_email", "user_tp");

    private static final List<String> TARGET_ACCOUNT_KEYWORDS = Arrays.asList("admin", "test");

    private static final Pattern[] XSS_PATTERNS = new Pattern[] {
            Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("src[\r\n]*=[\r\n]*['\"](.*?)['\"]",
                    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
            Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
            Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile(
                    "on(blur|change|click|dblclick|error|focus|keydown|keypress|keyup|load|mousedown|mousemove|mouseout|mouseover|mouseup|reset|select|submit|unload|toggle)=",
                    Pattern.CASE_INSENSITIVE)
    };

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchType = request.getParameter("searchType");
        String keyword = request.getParameter("keyword");
        String originalKeyword = keyword;

        boolean isSqliAttempt = isSqlInjectionAttempt(originalKeyword); // 원본 페이로드에 대한 공격 시도 여부
        String sanitizedKeyword = null;

        // Level 2의 취약한 필터링 로직은 그대로 유지
        if (keyword != null) {
            sanitizedKeyword = keyword.replace("select", "")
                    .replace("union", "")
                    .replace("and", "")
                    .replace("or", "")
                    .replace("where", "")
                    .replace("update", "")
                    .replace("alert", "")
                    .replace("prompt", "")
                    .replace("confirm", "")
                    .replace("console", "")
                    .replace("window", "")
                    .replace("cookie", "")
                    .replace("onclick", "")
                    .replace("onerror", "")
                    .replace("javascript:", "");

            // XSS 필터 우회 성공 시 피드백 (필터링 후에도 XSS 패턴이 남아있으면 성공으로 간주)
            if (checkXssPayload(originalKeyword) && checkXssPayload(sanitizedKeyword)) {
                FeedbackUtil.setFeedback(request, "XSS", "XSS 필터 우회 성공", "단순 문자열 치환 필터를 우회하여 XSS 페이로드 주입에 성공했습니다.",
                        originalKeyword);
            }
            keyword = sanitizedKeyword;
        } else {
            sanitizedKeyword = ""; // keyword가 null일 경우 sanitizedKeyword도 초기화
        }

        // SQLi 필터 우회 성공 여부 판단 (원본은 공격인데, 필터링 후에도 공격 패턴이 남아있는 경우)
        boolean isSqliBypassSuccess = isSqliAttempt && isSqlInjectionAttempt(sanitizedKeyword);

        try {
            BoardDAO boardDAO = BoardDAO.getInstance();
            String pageStr = request.getParameter("page");
            int page = (pageStr == null || pageStr.isEmpty()) ? 1 : Integer.parseInt(pageStr);
            int pageSize = 10;

            int totalCount = boardDAO.getBoardCountLevel2(searchType, keyword);
            Pagination pagination = new Pagination(page, pageSize, totalCount);
            List<BoardVO> boardList = boardDAO.selectAllBoardsLevel2(page, pageSize, searchType, keyword);

            // SQL Injection 필터 우회 성공 시 피드백
            if (isSqliBypassSuccess && boardList != null && !boardList.isEmpty()) {
                StringBuilder executedData = new StringBuilder();
                executedData.append("사용한 페이로드:\n").append(originalKeyword).append("\n\n추출 및 조작된 데이터 (상위 5개):\n");
                executedData.append("SEQ | TITLE | AUTHOR | UPLOAD_TIME | UPDATE_TIME | LEVEL\n");
                executedData.append("----------------------------------------------------------\n");
                for (int i = 0; i < Math.min(boardList.size(), 5); i++) {
                    BoardVO vo = boardList.get(i);
                    executedData.append(String.format("%s | %s | %s | %s | %s | %s\n",
                            vo.getBoardSeq(), vo.getBoardTitle(), vo.getBoardAuthor(),
                            vo.getUploadTime(), vo.getUpdateTime(), vo.getBoardLevel()));
                }
                FeedbackUtil.setFeedback(request, "SQLI", "SQL Injection 필터 우회 성공",
                        "단순 문자열 치환 필터를 우회하여 SQL Injection 공격에 성공했습니다.", executedData.toString());
            }

            // 게시글 목록 자체에 포함된 XSS 페이로드 검사 (다른 피드백이 없을 때만)
            if (request.getSession().getAttribute("feedback_type") == null) {
                if (boardList != null) {
                    for (BoardVO board : boardList) {
                        if (checkXssPayload(board.getBoardTitle())) { // Level 2는 XSS 필터링이 취약하여 제목에 XSS가 주입될 수 있음
                            FeedbackUtil.setFeedback(request, "XSS", "XSS 공격 성공",
                                    "게시글 제목에 XSS 페이로드가 성공적으로 주입되었습니다.", board.getBoardTitle());
                            break; // 첫 번째 페이로드 발견 시 중단
                        }
                    }
                }
            }

            request.setAttribute("boardList", boardList);
            request.setAttribute("pagination", pagination);
            request.setAttribute("searchType", searchType);
            request.setAttribute("keyword", originalKeyword); // 화면에는 필터링 전 원본 키워드 표시

            RequestDispatcher dispatcher = request.getRequestDispatcher("/playground/level2/list.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String errorStackTrace = sw.toString();
            Throwable cause = e;
            boolean isSqlError = false;
            String sqlErrorMessage = "";

            while (cause != null) {
                if (cause instanceof java.sql.SQLException) {
                    isSqlError = true;
                    sqlErrorMessage = cause.getMessage();
                    break;
                }
                cause = cause.getCause();
            }

            // Error-Based SQL Injection 성공 시 피드백
            if (isSqlError && isSqliAttempt) {
                StringBuilder executedData = new StringBuilder();
                executedData.append("사용한 페이로드:\n").append(originalKeyword).append("\n\n");
                executedData.append("데이터베이스 에러 노출:\n").append(sqlErrorMessage);

                FeedbackUtil.setFeedback(request, "SQLI", "Error-Based SQL Injection 성공",
                        "의도적으로 유발된 데이터베이스 에러 메시지를 통해 내부 정보가 노출되었습니다.", executedData.toString()); 
            } else if (isSqliAttempt) { // SQLI 시도했으나 SQL 에러는 아닌 경우
                FeedbackUtil.setFeedback(request, "SQLI", "SQL Injection 공격 시도",
                        "SQL Injection 페이로드가 감지되었으나, 데이터베이스 에러는 발생하지 않았습니다.", originalKeyword);
            }
            
            // 공통적으로 사용자에게 오류 알림
            NotificationUtil.addNotification(request, "게시글 목록 조회 중 오류가 발생했습니다.", "error");
            
            // 피드백 또는 알림을 표시하기 위해 리다이렉트
            response.sendRedirect(request.getContextPath() + "/playground/level2/list.do");
        }
    }

    private boolean isSqlInjectionAttempt(String keyword) {
        if (keyword == null || keyword.trim().isEmpty())
            return false;
        String lowerKeyword = keyword.toLowerCase();

        for (String schema : DB_SCHEMA_KEYWORDS) {
            if (lowerKeyword.contains(schema))
                return true;
        }
        for (String account : TARGET_ACCOUNT_KEYWORDS) {
            if (lowerKeyword.contains("'" + account + "'") || lowerKeyword.contains("\"" + account + "\""))
                return true;
        }
        for (String pattern : COMPREHENSIVE_SQLI_KEYWORDS) {
            if (lowerKeyword.contains(pattern))
                return true;
        }
        return false;
    }

    private boolean checkXssPayload(String value) {
        if (value == null || value.trim().isEmpty())
            return false;
        for (Pattern pattern : XSS_PATTERNS) {
            if (pattern.matcher(value).find())
                return true;
        }
        return false;
    }
}