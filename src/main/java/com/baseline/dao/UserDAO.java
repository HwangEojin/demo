package com.baseline.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.baseline.config.DBConnection;
import com.baseline.dto.UserVO;

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
        UserVO userVO = null;
        String sql = "SELECT u.USER_SEQ, u.USER_ID, u.USER_PW, u.USER_NM, u.USER_NUM, u.USER_EMAIL, a.USER_TP " +
                     "FROM TB_USER u JOIN TB_AUTH a ON u.USER_SEQ = a.USER_SEQ " +
                     "WHERE u.USER_ID = ? AND u.USER_PW = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            pstmt.setString(2, userPw);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    userVO = new UserVO();
                    userVO.setUserSeq(rs.getInt("USER_SEQ"));
                    userVO.setUserId(rs.getString("USER_ID"));
                    userVO.setUserPw(rs.getString("USER_PW"));
                    userVO.setUserNm(rs.getString("USER_NM"));
                    userVO.setUserNum(rs.getString("USER_NUM"));
                    userVO.setUserEmail(rs.getString("USER_EMAIL"));
                    userVO.setUserTp(rs.getString("USER_TP"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 실제 프로덕션에서는 로깅 프레임워크 사용을 권장합니다.
        }
        return userVO;
    }

    /**
     * 회원 가입 처리를 위한 메소드 (Transaction 처리)
     * @param userVO 가입할 사용자 정보
     * @return int - 성공 시 1, 실패 시 0 또는 -1
     */
    public int signup(UserVO userVO) {
        String sqlUser = "INSERT INTO TB_USER(USER_ID, USER_PW, USER_NM, USER_NUM, USER_EMAIL) VALUES(?, ?, ?, ?, ?)";
        String sqlAuth = "INSERT INTO TB_AUTH(USER_SEQ, USER_TP) VALUES(LAST_INSERT_ID(), ?)";
        int result = 0;

        try (Connection conn = DBConnection.getInstance().getConnection()) {
            // 트랜잭션 시작
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtUser = conn.prepareStatement(sqlUser);
                 PreparedStatement pstmtAuth = conn.prepareStatement(sqlAuth)) {

                // TB_USER에 삽입
                pstmtUser.setString(1, userVO.getUserId());
                pstmtUser.setString(2, userVO.getUserPw());
                pstmtUser.setString(3, userVO.getUserNm());
                pstmtUser.setString(4, userVO.getUserNum());
                pstmtUser.setString(5, userVO.getUserEmail());
                pstmtUser.executeUpdate();

                // TB_AUTH에 삽입 (기본 권한 'B' 일반유저)
                pstmtAuth.setString(1, "B"); 
                result = pstmtAuth.executeUpdate();

                conn.commit(); // 트랜잭션 커밋

            } catch (SQLException e) {
                conn.rollback(); // 오류 발생 시 롤백
                e.printStackTrace();
                return -1; // 실패 반환
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // 실패 반환
        }
        return result;
    }
}