package com.baseline.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
  private static DBConnection instance;

  // DB 접속 정보
  private final String URL = "jdbc:mysql://localhost:3306/baselinedb?serverTimezone=UTC&useSSL=false";
  private final String USER = "baseline";
  private final String PASSWORD = "base1234";

  // 생성자를 private으로 막아 외부에서 new 인스턴스화 차단
  private DBConnection() {
    try {
      // MySQL 드라이버 로드
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public static synchronized DBConnection getInstance() {
    if (instance == null) {
      instance = new DBConnection();
    }
    return instance;
  }

  // Connection 객체 반환
  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }
}