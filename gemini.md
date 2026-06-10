기능 요구사항

1.회원 가입

2.로그인

3.게시판 CRUD

4.유저 관리 페이지 (관리자용)

5.게시판 관리 페이지 (관리자용)


DB 설계

## DB 테이블

### 회원 테이블 ( TB_USER )

| USER_SEQ 
( PK ) | USER_ID 
( UNIQ, NN ) | USER_PW
( NN ) | USER_NM
( NN ) | USER_NUM
( NN ) | USER_EMAIL
( NN ) |
| --- | --- | --- | --- | --- | --- |
| 0 | admin | admin | 관리자 | 010-0000-0000 | admin@baseline.co.kr |
| 1 | test | test | 유저 | 010-1111-1111 | user@baseline.co.kr |

### 계정 권한 ( TB_AUTH )

| USER_SEQ ( PK,FK ) | USER_TP (NN) |
| --- | --- |
| 0 | A ( 관리자 ) |
| 1 | B ( 일반 ) |

### 게시판 테이블 ( TB_BOARD )

| BOARD_SEQ
( PK )
( NN ) | BOARD_TITLE
( NN ) | BOARD_CONTENTS
( NN ) | BOARD_AUTHOR
( NN ) | UPLOAD_TIME
( NN ) | UPDATE_TIME |
| --- | --- | --- | --- | --- | --- |
| 0 | 게시판 제목 | 게시판 내용 | 작성자 | 등록일 | 수정일 |

### 첨부파일 테이블 ( TB_ATTACH )

| FILE_SEQ ( PK ) (UNIQ, NN) | BOARD_SEQ ( FK ) (UNIQ, NN) | FILE_NM (NN) |
| --- | --- | --- |
| 0 | 0 | Random UUID |