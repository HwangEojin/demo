package com.baseline.dto;

/**
 * TB_USER, TB_AUTH 테이블 정보를 담는 VO(Value Object)
 */
public class UserVO {
    private int userSeq;
    private String userId;
    private String userPw;
    private String userNm;
    private String userNum;
    private String userEmail;
    private String userTp; // 'A': 관리자, 'B': 일반

    // Getters and Setters
    public int getUserSeq() {
        return userSeq;
    }

    public void setUserSeq(int userSeq) {
        this.userSeq = userSeq;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPw() {
        return userPw;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }

    public String getUserNm() {
        return userNm;
    }

    public void setUserNm(String userNm) {
        this.userNm = userNm;
    }

    public String getUserNum() {
        return userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserTp() {
        return userTp;
    }

    public void setUserTp(String userTp) {
        this.userTp = userTp;
    }
}