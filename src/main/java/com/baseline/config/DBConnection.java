package com.baseline.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
  private static DBConnection instance;
  
  private final String URL = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:mysql://localhost:3306/baselinedb?serverTimezone=Asia/Seoul&useSSL=false&allowPublicKeyRetrieval=true";
  private final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "baseline";
  private final String PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "base1234";

  static {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Failed to load MySQL JDBC driver.", e);
    }
  }
  private DBConnection() {
  }
  public static synchronized DBConnection getInstance() {
    if (instance == null) {
      instance = new DBConnection();
    }
    return instance;
  }

  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }
}