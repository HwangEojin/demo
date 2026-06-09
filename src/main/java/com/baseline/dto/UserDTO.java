package com.baseline.dto;

public class UserDTO {
  private int userSeq;
  private String userId;
  private String userPw;
  private String userNm;
  private String userNum;
  private String userEmail;
  private String userTp;

  public UserDTO() {
  }

  // Getter / Setter 메서드
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