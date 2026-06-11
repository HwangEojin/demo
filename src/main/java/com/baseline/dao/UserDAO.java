package com.baseline.dao;

import com.baseline.dto.UserVO;
// import com.baseline.util.DBManager; // DB 커넥션 유틸리티

/**
 * TB_USER, TB_AUTH 테이블에 대한 DB 작업을 처리하는 DAO
 */
public class UserDAO {

    private static UserDAO instance = new UserDAO();

    private UserDAO() {}

    public static UserDAO getInstance() {
        return instance;
    }

    /**
     * 사용자 로그인 처리를 위한 메소드
     * @param userId 사용자 아이디
     * @param userPw 사용자 비밀번호
     * @return UserVO - 로그인 성공 시 사용자 정보, 실패 시 null
     */
    public UserVO login(String userId, String userPw) {
        // TODO: DB에서 아이디와 비밀번호가 일치하는 사용자 정보 조회
        // SQL: SELECT * FROM TB_USER u JOIN TB_AUTH a ON u.USER_SEQ = a.USER_SEQ WHERE u.USER_ID = ? AND u.USER_PW = ?
        System.out.println("로그인 처리 로직 구현 필요");
        return null;
    }

    // TODO: 회원 가입, 회원 목록 조회(관리자), 회원 정보 수정 등 추가 메소드 구현
}