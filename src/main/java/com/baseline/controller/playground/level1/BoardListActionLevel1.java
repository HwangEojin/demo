package com.baseline.controller.playground.level1;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.List;

import com.baseline.controller.Action;
import com.baseline.dao.BoardDAO;
import com.baseline.dto.BoardVO;
import com.baseline.util.FeedbackUtil;
import com.baseline.dto.Pagination;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BoardListActionLevel1 implements Action {

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
        String keyword = request.getParameter("keyword");

        // XSS 페이로드 검사 및 세션 세팅
        if (checkXssPayload(keyword)) {
            FeedbackUtil.setFeedback(request, "XSS", "XSS 공격 성공",
                    "XSS 페이로드 주입에 성공했습니다.", keyword);
        }

        String normalizedKeyword = (keyword != null) ? keyword.toLowerCase().replaceAll("\\s+", " ") : "";
        boolean isSqliAttempt = isSqlInjectionAttempt(normalizedKeyword);

        try {
            BoardDAO boardDAO = BoardDAO.getInstance();

            String pageStr = request.getParameter("page");
            int page = (pageStr == null || pageStr.isEmpty()) ? 1 : Integer.parseInt(pageStr);
            int pageSize = 10;

            int totalCount = isSqliAttempt ? 100 : boardDAO.getBoardCountLevel1(searchType, keyword);
            Pagination pagination = new Pagination(page, pageSize, totalCount);

            List<BoardVO> boardList = boardDAO.selectAllBoardsLevel1(page, pageSize, searchType, keyword);

            if (isSqliAttempt && boardList != null && !boardList.isEmpty()) {
                StringBuilder executedData = new StringBuilder();
                executedData.append("사용한 페이로드:\n").append(keyword).append("\n\n추출 및 조작된 데이터 (상위 5개):\n");
                executedData.append("SEQ | TITLE | AUTHOR | UPLOAD_TIME | UPDATE_TIME | LEVEL\n"); 
                executedData.append("----------------------------------------------------------\n");
                for (int i = 0; i < Math.min(boardList.size(), 5); i++) {
                    BoardVO vo = boardList.get(i);
                    executedData.append(String.format("%s | %s | %s | %s | %s | %s\n",
                            vo.getBoardSeq(), vo.getBoardTitle(), vo.getBoardAuthor(),
                            vo.getUploadTime(), vo.getUpdateTime(), vo.getBoardLevel()));
                }
                FeedbackUtil.setFeedback(request, "SQLI", "SQL Injection 성공 (데이터 노출)",
                        "SQL 쿼리가 조작되어 정상적인 응답의 결과 집합(Result Set)이 변조되거나 탈취되었습니다.", executedData.toString());
            }

            // 게시글 목록 자체에 포함된 XSS 페이로드 검사 (다른 피드백이 없을 때만)
            if (request.getSession().getAttribute("feedback_type") == null) {
                if (boardList != null) {
                    for (BoardVO board : boardList) {
                        if (checkXssPayload(board.getBoardTitle())) { // Level 1은 XSS 필터링이 없어 제목에 XSS가 주입될 수 있음
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
            request.setAttribute("keyword", keyword);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/playground/level1/list.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String errorStackTrace = sw.toString();

            Throwable cause = e;
            boolean isSqlError = false;
            String sqlErrorMessage = "";
            
            while (cause != null) {
                if (cause instanceof SQLException) {
                    isSqlError = true;
                    sqlErrorMessage = cause.getMessage();
                    break;
                }
                cause = cause.getCause();
            }

            if (isSqlError && isSqliAttempt) {
                StringBuilder executedData = new StringBuilder();
                executedData.append("사용한 페이로드:\n").append(keyword).append("\n\n");
                executedData.append("데이터베이스 에러 노출:\n").append(sqlErrorMessage);

                FeedbackUtil.setFeedback(request, "SQLI", "Error-Based SQL Injection 성공",
                        "의도적으로 유발된 데이터베이스 에러 메시지를 통해 내부 정보가 노출되었습니다.", executedData.toString());
            }

            request.setAttribute("dbError", sqlErrorMessage + "\n\n" + errorStackTrace); 
            request.setAttribute("boardList", null);
            request.setAttribute("pagination", new Pagination(1, 10, 0));
            request.setAttribute("searchType", searchType);
            request.setAttribute("keyword", keyword);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/playground/level1/list.jsp");
            try {
                dispatcher.forward(request, response);
            } catch (Exception forwardEx) {
                response.setContentType("text/plain; charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().print("치명적 서버 오류 발생: \n" + errorStackTrace);
            }
        }
    }

    private boolean isSqlInjectionAttempt(String normalizedKeyword) {
        if (normalizedKeyword == null || normalizedKeyword.isEmpty()) return false;
        
        for (String pattern : COMPREHENSIVE_SQLI_KEYWORDS) {
            if (normalizedKeyword.contains(pattern)) return true;
        }
        for (String schema : DB_SCHEMA_KEYWORDS) {
            if (normalizedKeyword.contains(schema)) return true;
        }
        for (String account : TARGET_ACCOUNT_KEYWORDS) {
            if (normalizedKeyword.contains("'" + account + "'") || normalizedKeyword.contains("\"" + account + "\"")) return true;
        }
        return false;
    }

    private boolean checkXssPayload(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        for (Pattern pattern : XSS_PATTERNS) {
            if (pattern.matcher(value).find()) {
                return true;
            }
        }
        return false;
    }
}