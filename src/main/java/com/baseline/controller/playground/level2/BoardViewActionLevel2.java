package com.baseline.controller.playground.level2;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import com.baseline.controller.Action;
import com.baseline.dao.BoardDAO;
import com.baseline.dao.FileDAO;
import com.baseline.dto.BoardVO;
import com.baseline.dto.FileVO;

import com.baseline.util.NotificationUtil;
import com.baseline.util.FeedbackUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BoardViewActionLevel2 implements Action {
    
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
        try {
            String boardSeqStr = (String) request.getAttribute("boardSeq");
            if (boardSeqStr == null || boardSeqStr.isEmpty()) {
                NotificationUtil.addNotification(request, "잘못된 요청입니다. 게시글 번호가 없습니다.", "error");
                response.sendRedirect(request.getContextPath() + "/playground/level2/list.do");
                return;
            }
    
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
    
            if (board != null) {
                FileDAO fileDAO = FileDAO.getInstance();
                List<FileVO> fileList = fileDAO.getFilesByBoardSeq(boardSeq);
                request.setAttribute("fileList", fileList);
            }
    
            request.setAttribute("board", board);
            request.getRequestDispatcher("/playground/level2/view.jsp").forward(request, response);
        } catch (Exception e) {
            NotificationUtil.addNotification(request, "게시글 조회 중 오류가 발생했습니다: " + e.getMessage(), "error");
            response.sendRedirect(request.getContextPath() + "/playground/level2/list.do");
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