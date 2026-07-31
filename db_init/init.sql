-- 0. 제로 인덱스(0) 자동 치환 방지 설정
SET SESSION sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

-- 1. 데이터베이스 생성 및 선택
CREATE DATABASE IF NOT EXISTS baselinedb;
USE baselinedb;

-- 2. 테이블 생성 (DDL)
CREATE TABLE IF NOT EXISTS TB_USER (
    user_seq INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL UNIQUE,
    user_pw VARCHAR(100) NOT NULL,
    user_nm VARCHAR(50) NOT NULL,
    user_num VARCHAR(20) NOT NULL,
    user_email VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS TB_AUTH (
    user_seq INT NOT NULL,
    user_tp CHAR(1) NOT NULL,
    PRIMARY KEY (user_seq),
    FOREIGN KEY (user_seq) REFERENCES TB_USER(user_seq) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS TB_BOARD (
    board_seq INT AUTO_INCREMENT PRIMARY KEY,
    board_title VARCHAR(200) NOT NULL,
    board_contents TEXT NOT NULL,
    board_author VARCHAR(50) NOT NULL,
    board_level INT,
    upload_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS TB_ATTACH (
    file_seq INT AUTO_INCREMENT PRIMARY KEY,
    board_seq INT NOT NULL,
    ORIGINAL_FILE_NM VARCHAR(255) NOT NULL,
    SAVED_FILE_NM VARCHAR(255) NOT NULL,
    FOREIGN KEY (board_seq) REFERENCES TB_BOARD(board_seq) ON DELETE CASCADE
);

-- 3. 초기 데이터 삽입 (DML)
INSERT IGNORE INTO TB_USER (user_seq, user_id, user_pw, user_nm, user_num, user_email) VALUES
(0, 'admin', 'admin1234', '관리자', '010-0000-0000', 'admin@example.com'),
(1, 'testuser', 'test1234', '테스트사용자', '010-1111-2222', 'test@example.com');

INSERT IGNORE INTO TB_AUTH (user_seq, user_tp) VALUES
(0, 'A'), 
(1, 'B'); 

INSERT IGNORE INTO TB_BOARD (board_title, board_contents, board_author, board_level) VALUES
('Welcome to Baseline Board Level 1', 'This is a sample post for Level 1.', 'admin', 1),
('Welcome to Baseline Board Level 2', 'This is a sample post for Level 2.', 'admin', 2),
('Welcome to Baseline Board Level 3', 'This is a sample post for Level 3.', 'admin', 3);