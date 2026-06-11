<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입</title>
<style>
    body { font-family: sans-serif; text-align: center; padding-top: 50px; }
    form { display: inline-block; text-align: left; border: 1px solid #ccc; padding: 20px; border-radius: 8px; }
    input { margin-bottom: 10px; width: 250px; padding: 5px; }
    input[type="submit"] { width: 264px; cursor: pointer; }
</style>
</head>
<body>
    <h2>회원가입</h2>
    <form action="signup.do" method="post">
        <label for="userId">아이디:</label><br>
        <input type="text" id="userId" name="userId" required><br>
        <label for="userPw">비밀번호:</label><br>
        <input type="password" id="userPw" name="userPw" required><br>
        <label for="userNm">이름:</label><br>
        <input type="text" id="userNm" name="userNm" required><br>
        <label for="userNum">연락처:</label><br>
        <input type="text" id="userNum" name="userNum" placeholder="010-1234-5678" required><br>
        <label for="userEmail">이메일:</label><br>
        <input type="email" id="userEmail" name="userEmail" required><br>
        <input type="submit" value="가입하기">
    </form>
    <p style="color:red;">${message}</p>
    <p><a href="login_form.do">로그인 페이지로 돌아가기</a></p>
</body>
</html>