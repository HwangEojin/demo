package com.baseline.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
            e.printStackTrace();
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

    /**
     * 사용자 아이디 중복 여부를 확인하는 메소드
     * @param userId 확인할 사용자 아이디
     * @return boolean - 중복 시 true, 아닐 시 false
     */
    public boolean isUserIdExists(String userId) {
        String sql = "SELECT COUNT(*) FROM TB_USER WHERE USER_ID = ?";
        boolean exists = false;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    exists = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }

    /**
     * 전체 사용자 수를 조회하는 메소드 (관리자용)
     * @return int - 전체 사용자 수
     */
    public int getUserCount(String searchType, String keyword) {
        int count = 0;
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM TB_USER u");
        boolean hasKeyword = (keyword != null && !keyword.trim().isEmpty());

        if (hasKeyword) {
            if ("userId".equals(searchType)) {
                sql.append(" WHERE u.USER_ID LIKE ?");
            } else if ("userNm".equals(searchType)) {
                sql.append(" WHERE u.USER_NM LIKE ?");
            }
        }

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            if (hasKeyword) {
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
     * 페이징 처리된 모든 사용자 목록을 조회하는 메소드 (관리자용)
     * @param page - 현재 페이지
     * @param limit - 페이지당 사용자 수
     * @return List<UserVO> - 사용자 정보 리스트
     */
    public List<UserVO> selectAllUsers(int page, int limit, String searchType, String keyword) {
        List<UserVO> userList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT u.USER_SEQ, u.USER_ID, u.USER_NM, u.USER_NUM, u.USER_EMAIL, a.USER_TP ")
            .append("FROM TB_USER u JOIN TB_AUTH a ON u.USER_SEQ = a.USER_SEQ ");
        boolean hasKeyword = (keyword != null && !keyword.trim().isEmpty());

        if (hasKeyword) {
            if ("userId".equals(searchType)) {
                sql.append(" WHERE u.USER_ID LIKE ?");
            } else if ("userNm".equals(searchType)) {
                sql.append(" WHERE u.USER_NM LIKE ?");
            }
        }
        sql.append(" ORDER BY u.USER_SEQ DESC LIMIT ? OFFSET ?");

        int offset = (page - 1) * limit;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (hasKeyword) {
                pstmt.setString(paramIndex++, "%" + keyword + "%");
            }
            pstmt.setInt(paramIndex++, limit);
            pstmt.setInt(paramIndex, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    UserVO userVO = new UserVO();
                    userVO.setUserSeq(rs.getInt("USER_SEQ"));
                    userVO.setUserId(rs.getString("USER_ID"));
                    userVO.setUserNm(rs.getString("USER_NM"));
                    userVO.setUserNum(rs.getString("USER_NUM"));
                    userVO.setUserEmail(rs.getString("USER_EMAIL"));
                    userVO.setUserTp(rs.getString("USER_TP"));
                    userList.add(userVO);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    /**
     * 사용자의 권한을 변경하는 메소드 (관리자용)
     * @param userSeq - 권한을 변경할 사용자의 SEQ
     * @param userTp - 새로운 권한 ('A' 또는 'B')
     */
    public void updateUserAuth(int userSeq, String userTp) {
        String sql = "UPDATE TB_AUTH SET USER_TP = ? WHERE USER_SEQ = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userTp);
            pstmt.setInt(2, userSeq);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 사용자를 삭제하는 메소드 (관리자용)
     * @param userSeq - 삭제할 사용자의 SEQ
     */
    public void deleteUser(int userSeq) {
        String deleteAuthSQL = "DELETE FROM TB_AUTH WHERE USER_SEQ = ?";
        String deleteUserSQL = "DELETE FROM TB_USER WHERE USER_SEQ = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // TB_AUTH 테이블에서 먼저 삭제
            try (PreparedStatement pstmtAuth = conn.prepareStatement(deleteAuthSQL)) {
                pstmtAuth.setInt(1, userSeq);
                pstmtAuth.executeUpdate();
            }

            // TB_USER 테이블에서 삭제
            try (PreparedStatement pstmtUser = conn.prepareStatement(deleteUserSQL)) {
                pstmtUser.setInt(1, userSeq);
                pstmtUser.executeUpdate();
            }

            conn.commit(); // 트랜잭션 커밋
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /**
     * 사용자 정보를 수정하는 메소드
     * @param userVO - 수정할 사용자 정보
     * @return int - 성공 시 1, 실패 시 0
     */
    public int updateUser(UserVO userVO) {
        String sql = "UPDATE TB_USER SET USER_PW = ?, USER_NM = ?, USER_EMAIL = ?, USER_NUM = ? WHERE USER_SEQ = ?";
        int result = 0;
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userVO.getUserPw());
            pstmt.setString(2, userVO.getUserNm());
            pstmt.setString(3, userVO.getUserEmail());
            pstmt.setString(4, userVO.getUserNum());
            pstmt.setInt(5, userVO.getUserSeq());

            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * USER_SEQ로 사용자 정보를 조회하는 메소드
     * @param userSeq - 조회할 사용자 SEQ
     * @return UserVO - 사용자 정보
     */
    public UserVO selectUserBySeq(int userSeq) {
        UserVO userVO = null;
        String sql = "SELECT u.USER_SEQ, u.USER_ID, u.USER_PW, u.USER_NM, u.USER_NUM, u.USER_EMAIL, a.USER_TP " +
                     "FROM TB_USER u JOIN TB_AUTH a ON u.USER_SEQ = a.USER_SEQ " +
                     "WHERE u.USER_SEQ = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userSeq);
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
            e.printStackTrace();
        }
        return userVO;
    }
}