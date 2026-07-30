package com.baseline.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.baseline.config.DBConnection;
import com.baseline.dto.BoardVO;

public class BoardDAO {

    private static BoardDAO instance = new BoardDAO();

    private BoardDAO() {}

    public static BoardDAO getInstance() {
        return instance;
    }

    // --- Level 1 : SQL Injection에 취약한 메소드 ---

    /**
     * [Level 1] 검색 조건에 맞는 게시글 수를 조회하는 메소드
     * @param searchType 검색 타입 (title, author)
     * @param keyword 검색어
     * @return int - 게시글 수
     */
    public int getBoardCountLevel1(String searchType, String keyword) throws SQLException {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM TB_BOARD WHERE BOARD_LEVEL = 1";
        
        if (keyword != null && !keyword.trim().isEmpty() && searchType != null && !searchType.trim().isEmpty()) {
            String dbSearchColumn;
            switch (searchType) {
                case "title":
                    dbSearchColumn = "BOARD_TITLE";
                    break;
                case "author":
                    dbSearchColumn = "BOARD_AUTHOR";
                    break;
                default:
                    dbSearchColumn = null;
            }
            if (dbSearchColumn != null) {
                sql += " AND " + dbSearchColumn + " LIKE '%" + keyword + "%'";
            }
        }

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } 
        return count;
    }

    /**
     * [Level 1] 페이징 및 검색 기능이 포함된 게시판 목록 조회 메소드
     * @param page 현재 페이지
     * @param limit 페이지당 게시글 수
     * @param searchType 검색 타입
     * @param keyword 검색어
     * @return List<BoardVO> - 게시판 글 목록
     */
    public List<BoardVO> selectAllBoardsLevel1(int page, int limit, String searchType, String keyword) throws SQLException {
        List<BoardVO> boardList = new ArrayList<>();
        int offset = (page - 1) * limit;

        String sql = "SELECT BOARD_SEQ, BOARD_TITLE, BOARD_AUTHOR, UPLOAD_TIME, UPDATE_TIME, BOARD_LEVEL FROM TB_BOARD WHERE BOARD_LEVEL = 1 ";
        if (keyword != null && !keyword.trim().isEmpty() && searchType != null && !searchType.trim().isEmpty()) {
            String dbSearchColumn;
            switch (searchType) {
                case "title":
                    dbSearchColumn = "BOARD_TITLE";
                    break;
                case "author":
                    dbSearchColumn = "BOARD_AUTHOR";
                    break;
                default:
                    dbSearchColumn = null;
            }
            if (dbSearchColumn != null) {
                sql += " AND " + dbSearchColumn + " LIKE '%" + keyword + "%'";
            }
        }
        sql += " ORDER BY BOARD_SEQ DESC LIMIT " + limit + " OFFSET " + offset;

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                BoardVO boardVO = new BoardVO();
                boardVO.setBoardSeq(rs.getInt("BOARD_SEQ"));
                boardVO.setBoardTitle(rs.getString("BOARD_TITLE"));
                boardVO.setBoardAuthor(rs.getString("BOARD_AUTHOR"));
                boardVO.setUploadTime(rs.getTimestamp("UPLOAD_TIME"));
                boardVO.setUpdateTime(rs.getTimestamp("UPDATE_TIME"));
                boardVO.setBoardLevel(rs.getInt("BOARD_LEVEL"));
                boardList.add(boardVO);
            }
        } 
        return boardList;
    }

    // --- Level 2 : SQL Injection에 취약한 메소드 ---

    /**
     * [Level 2] 검색 조건에 맞는 게시글 수를 조회하는 메소드 
     * @param searchType 검색 타입 (title, author)
     * @param keyword 검색어
     * @return int - 게시글 수
     */
    public int getBoardCountLevel2(String searchType, String keyword) throws SQLException {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM TB_BOARD WHERE BOARD_LEVEL = 2";
        
        if (keyword != null && !keyword.trim().isEmpty() && searchType != null && !searchType.trim().isEmpty()) {
            String dbSearchColumn;
            switch (searchType) {
                case "title":
                    dbSearchColumn = "BOARD_TITLE";
                    break;
                case "author":
                    dbSearchColumn = "BOARD_AUTHOR";
                    break;
                default:
                    dbSearchColumn = null;
            }
            if (dbSearchColumn != null) {
                sql += " AND " + dbSearchColumn + " LIKE '%" + keyword + "%'";
            }
        }

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } 
        return count;
    }

    /**
     * [Level 2] 페이징 및 검색 기능이 포함된 게시판 목록 조회 메소드
     * @param page 현재 페이지
     * @param limit 페이지당 게시글 수
     * @param searchType 검색 타입
     * @param keyword 검색어
     * @return List<BoardVO> - 게시판 글 목록
     */
    public List<BoardVO> selectAllBoardsLevel2(int page, int limit, String searchType, String keyword) throws SQLException {
        List<BoardVO> boardList = new ArrayList<>();
        int offset = (page - 1) * limit;

        String sql = "SELECT BOARD_SEQ, BOARD_TITLE, BOARD_AUTHOR, UPLOAD_TIME, UPDATE_TIME, BOARD_LEVEL FROM TB_BOARD WHERE BOARD_LEVEL = 2 ";
        if (keyword != null && !keyword.trim().isEmpty() && searchType != null && !searchType.trim().isEmpty()) {
            String dbSearchColumn;
            switch (searchType) {
                case "title":
                    dbSearchColumn = "BOARD_TITLE";
                    break;
                case "author":
                    dbSearchColumn = "BOARD_AUTHOR";
                    break;
                default:
                    dbSearchColumn = null;
            }
            if (dbSearchColumn != null) {
                sql += " AND " + dbSearchColumn + " LIKE '%" + keyword + "%'";
            }
        }
        sql += " ORDER BY BOARD_SEQ DESC LIMIT " + limit + " OFFSET " + offset;

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                BoardVO boardVO = new BoardVO();
                boardVO.setBoardSeq(rs.getInt("BOARD_SEQ"));
                boardVO.setBoardTitle(rs.getString("BOARD_TITLE"));
                boardVO.setBoardAuthor(rs.getString("BOARD_AUTHOR"));
                boardVO.setUploadTime(rs.getTimestamp("UPLOAD_TIME"));
                boardVO.setUpdateTime(rs.getTimestamp("UPDATE_TIME"));
                boardVO.setBoardLevel(rs.getInt("BOARD_LEVEL"));
                boardList.add(boardVO);
            }
        } 
        return boardList;
    }

    /**
     * 게시글 번호로 게시글을 삭제하는 범용 메소드
     * @param boardSeq - 삭제할 게시글 번호
     */
    public void deleteBoardBySeq(int boardSeq) throws SQLException {
        String sql = "DELETE FROM TB_BOARD WHERE BOARD_SEQ = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, boardSeq);
            pstmt.executeUpdate();
        }
    }


    // --- 범용(Generic) 및 안전한(Secure) 메소드 ---
    // Level 2, 3, 4 등 SQL Injection으로부터 안전해야 하는 기능들이 공통으로 사용합니다.
    
    /**
     * 특정 레벨의 게시글 수를 조회하는 메소드
     * @param level 게시판 레벨
     * @return int - 게시글 수
     */
    public int getBoardCountByLevel(int level) throws SQLException {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM TB_BOARD WHERE BOARD_LEVEL = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, level);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } 
        return count;
    }

    /**
     * 특정 레벨의 게시판 목록을 페이징하여 조회하는 메소드
     * @param level 게시판 레벨
     * @param page 현재 페이지
     * @param pageSize 페이지당 게시글 수
     * @return List<BoardVO> - 게시판 글 목록
     */
    public List<BoardVO> selectAllBoardsByLevel(int level, int page, int pageSize) throws SQLException {
        List<BoardVO> boardList = new ArrayList<>();
        String sql = "SELECT BOARD_SEQ, BOARD_TITLE, BOARD_AUTHOR, UPLOAD_TIME, UPDATE_TIME, BOARD_LEVEL FROM TB_BOARD WHERE BOARD_LEVEL = ? ORDER BY BOARD_SEQ DESC LIMIT ? OFFSET ?";
        int offset = (page - 1) * pageSize;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, level);
            pstmt.setInt(2, pageSize);
            pstmt.setInt(3, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BoardVO boardVO = new BoardVO();
                    boardVO.setBoardSeq(rs.getInt("BOARD_SEQ"));
                    boardVO.setBoardTitle(rs.getString("BOARD_TITLE"));
                    boardVO.setBoardAuthor(rs.getString("BOARD_AUTHOR"));
                    boardVO.setUploadTime(rs.getTimestamp("UPLOAD_TIME"));
                    boardVO.setUpdateTime(rs.getTimestamp("UPDATE_TIME"));
                    boardVO.setBoardLevel(rs.getInt("BOARD_LEVEL"));
                    boardList.add(boardVO);
                }
            }
        } 
        return boardList;
    }

    /**
     * 게시글 번호로 상세 정보를 조회하는 범용 메소드
     * @param boardSeq - 조회할 게시글 번호
     * @return BoardVO - 게시글 상세 정보
     */
    public BoardVO selectBoardBySeq(int boardSeq) throws SQLException {
        BoardVO boardVO = null;
        String sql = "SELECT BOARD_SEQ, BOARD_TITLE, BOARD_CONTENTS, BOARD_AUTHOR, UPLOAD_TIME, UPDATE_TIME, BOARD_LEVEL FROM TB_BOARD WHERE BOARD_SEQ = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, boardSeq);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    boardVO = new BoardVO();
                    boardVO.setBoardSeq(rs.getInt("BOARD_SEQ"));
                    boardVO.setBoardTitle(rs.getString("BOARD_TITLE"));
                    boardVO.setBoardContents(rs.getString("BOARD_CONTENTS"));
                    boardVO.setBoardAuthor(rs.getString("BOARD_AUTHOR"));
                    boardVO.setUploadTime(rs.getTimestamp("UPLOAD_TIME"));
                    boardVO.setUpdateTime(rs.getTimestamp("UPDATE_TIME"));
                    boardVO.setBoardLevel(rs.getInt("BOARD_LEVEL"));
                }
            }
        } 
        return boardVO;
    }

    /**
     * 새로운 게시글을 등록하는 범용 메소드
     * @param boardVO - 등록할 게시글 정보
     * @return int - 생성된 게시글의 boardSeq
     */
    public int insertBoard(BoardVO boardVO) throws SQLException {
        String sql = "INSERT INTO TB_BOARD (BOARD_TITLE, BOARD_CONTENTS, BOARD_AUTHOR, BOARD_LEVEL, UPLOAD_TIME, UPDATE_TIME) VALUES (?, ?, ?, ?, NOW(), NOW())";
        int generatedKey = -1;
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, boardVO.getBoardTitle());
            pstmt.setString(2, boardVO.getBoardContents());
            pstmt.setString(3, boardVO.getBoardAuthor());
            pstmt.setInt(4, boardVO.getBoardLevel());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getInt(1);
                }
            }
        } 
        return generatedKey;
    }

    /**
     * 게시글 정보를 수정하는 범용 메소드
     * @param boardVO - 수정할 게시글 정보
     */
    public void updateBoard(BoardVO boardVO) throws SQLException {
        String sql = "UPDATE TB_BOARD SET BOARD_TITLE = ?, BOARD_CONTENTS = ?, UPDATE_TIME = NOW() WHERE BOARD_SEQ = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, boardVO.getBoardTitle());
            pstmt.setString(2, boardVO.getBoardContents());
            pstmt.setInt(3, boardVO.getBoardSeq());
            pstmt.executeUpdate();
        } 
    }

    // --- Level 3 : PreparedStatement를 사용하는 안전한 검색 메소드 ---

    /**
     * [Level 3] 검색 조건에 맞는 게시글 수를 조회하는 메소드
     * @return int - 게시글 수
     */
    public int getBoardCountLevel3(String searchType, String keyword) {
        int count = 0;
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM TB_BOARD WHERE BOARD_LEVEL = 3");
        boolean hasKeyword = (keyword != null && !keyword.trim().isEmpty() && searchType != null && !searchType.trim().isEmpty());

        if (hasKeyword) {
            if ("title".equals(searchType)) {
                sql.append(" AND BOARD_TITLE LIKE ?");
            } else if ("author".equals(searchType)) {
                sql.append(" AND BOARD_AUTHOR LIKE ?");
            }
        }

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            if (hasKeyword && ("title".equals(searchType) || "author".equals(searchType))) {
                pstmt.setString(1, "%" + keyword + "%");
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * [Level 3] 페이징 처리된 게시판 목록을 조회하는 메소드
     * @param page - 현재 페이지
     * @param limit - 페이지당 게시글 수
     * @return List<BoardVO> - 게시판 글 목록
     */
    public List<BoardVO> selectAllBoardsLevel3(int page, int limit, String searchType, String keyword) {
        List<BoardVO> boardList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT BOARD_SEQ, BOARD_TITLE, BOARD_AUTHOR, UPLOAD_TIME, UPDATE_TIME, BOARD_LEVEL FROM TB_BOARD WHERE BOARD_LEVEL = 3");
        boolean hasKeyword = (keyword != null && !keyword.trim().isEmpty() && searchType != null && !searchType.trim().isEmpty());

        if (hasKeyword) {
            if ("title".equals(searchType)) {
                sql.append(" AND BOARD_TITLE LIKE ?");
            } else if ("author".equals(searchType)) {
                sql.append(" AND BOARD_AUTHOR LIKE ?");
            }
        }
        sql.append(" ORDER BY BOARD_SEQ DESC LIMIT ? OFFSET ?");

        int offset = (page - 1) * limit;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (hasKeyword && ("title".equals(searchType) || "author".equals(searchType))) {
                pstmt.setString(paramIndex++, "%" + keyword + "%");
            }
            pstmt.setInt(paramIndex++, limit);
            pstmt.setInt(paramIndex, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BoardVO boardVO = new BoardVO();
                    boardVO.setBoardSeq(rs.getInt("BOARD_SEQ"));
                    boardVO.setBoardTitle(rs.getString("BOARD_TITLE"));
                    boardVO.setBoardAuthor(rs.getString("BOARD_AUTHOR"));
                    boardVO.setUploadTime(rs.getTimestamp("UPLOAD_TIME"));
                    boardVO.setUpdateTime(rs.getTimestamp("UPDATE_TIME"));
                    boardVO.setBoardLevel(rs.getInt("BOARD_LEVEL"));
                    boardList.add(boardVO);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return boardList;
    }
}