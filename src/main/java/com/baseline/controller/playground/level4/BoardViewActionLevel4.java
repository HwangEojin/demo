package com.baseline.controller.playground.level4;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import com.baseline.controller.Action;
import com.baseline.dao.BoardDAO;
import com.baseline.dao.FileDAO;
import com.baseline.util.NotificationUtil;
import com.baseline.util.FeedbackUtil;
import com.baseline.dto.BoardVO;
import com.baseline.dto.FileVO;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BoardViewActionLevel4 implements Action {

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
        String boardSeqStr = (String) request.getAttribute("boardSeq");
        if (boardSeqStr == null || boardSeqStr.isEmpty()) {
            NotificationUtil.addNotification(request, "잘못된 요청입니다. 게시글 번호가 없습니다.", "error");
            response.sendRedirect(request.getContextPath() + "/playground/level4/list.do");
            return;
        }

        try {
            int boardSeq = Integer.parseInt(boardSeqStr);

            BoardDAO boardDAO = BoardDAO.getInstance();
            BoardVO board = boardDAO.selectBoardBySeq(boardSeq);

            // XSS 페이로드 검사 (게시글 제목 또는 내용에 XSS가 성공적으로 주입되었는지 확인)
            if (board != null) {
                if (checkXssPayload(board.getBoardTitle())) {
                    FeedbackUtil.setFeedback(request, "XSS", "XSS 공격 성공",
                            "게시글 제목에 XSS 페이로드가 성공적으로 주입되었습니다.", board.getBoardTitle());
                } else if (checkXssPayload(board.getBoardContents())) {
                    FeedbackUtil.setFeedback(request, "XSS", "XSS 공격 성공",
                            "게시글 내용에 XSS 페이로드가 성공적으로 주입되었습니다.", board.getBoardContents());
                }
            }

            FileDAO fileDAO = FileDAO.getInstance();
            List<FileVO> fileList = fileDAO.getFilesByBoardSeq(boardSeq);

            request.setAttribute("board", board);
            request.setAttribute("fileList", fileList);

            // LFI(Local File Inclusion) 공격 시도 탐지 및 피드백
            String template = request.getParameter("template");
            if (template != null && (template.contains("../") || template.contains("..\\"))) {
                String rootPath = request.getServletContext().getRealPath("/");
                File requestedFile = new File(rootPath, template);
                String canonicalRootPath = new File(rootPath).getCanonicalPath();
                String canonicalRequestedPath = requestedFile.getCanonicalPath();

                if (!canonicalRequestedPath.startsWith(canonicalRootPath)
                        || requestedFile.getCanonicalPath().contains("WEB-INF")) {
                    FeedbackUtil.setFeedback(request, "PATH_TRAVERSAL", "LFI 공격 성공", // LFI는 Path Traversal의 일종이므로
                            "경로 조작(LFI)을 통해 서버의 다른 파일을 포함(include)하는 데 성공했습니다. .txt 파일에 JSP 코드를 삽입하여 악성파일로 활용할 수 있습니다.", template);
                }
            }

            RequestDispatcher dispatcher = request.getRequestDispatcher("/playground/level4/view.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            NotificationUtil.addNotification(request, "게시글을 불러오는 중 오류가 발생했습니다: " + e.getMessage(), "error");
            response.sendRedirect(request.getContextPath() + "/playground/level4/list.do");
        }
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