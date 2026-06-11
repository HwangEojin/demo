package com.baseline.dao;

import java.util.List;
import com.baseline.dto.BoardVO;

public class BoardDAO {

    private static BoardDAO instance = new BoardDAO();

    private BoardDAO() {}

    public static BoardDAO getInstance() {
        return instance;
    }

    /**
     * 게시판 목록을 조회하는 메소드
     * @return List<BoardVO> - 게시판 글 목록
     */
    public List<BoardVO> selectAllBoards() {
        // TODO: DB에서 게시판 전체 목록을 최신순으로 조회
        // SQL: SELECT * FROM TB_BOARD ORDER BY UPLOAD_TIME DESC
        System.out.println("게시판 목록 조회 로직 구현 필요");
        return null;
    }

    // TODO: 게시글 상세 보기, 등록, 수정, 삭제 등 추가 메소드 구현
}