<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>로그인</title>
<style>
    body { font-family: sans-serif; text-align: center; padding-top: 50px; }
    form { display: inline-block; text-align: left; border: 1px solid #ccc; padding: 20px; border-radius: 5px; }
    input { margin-bottom: 10px; width: 200px; padding: 5px; }
    input[type="submit"] { width: 214px; cursor: pointer; }
</style>
</head>
<body>
    <h2>로그인</h2>
    <form action="login.do" method="post">
        <label for="userId">아이디:</label><br>
        <input type="text" id="userId" name="userId" required><br>
        <label for="userPw">비밀번호:</label><br>
        <input type="password" id="userPw" name="userPw" required><br>
        <input type="submit" value="로그인"><br>
    </form>
    <p style="color:red;">${requestScope.message}</p>
    <p style="color:blue;">${sessionScope.message}</p>
    <% session.removeAttribute("message"); %>
    <p><a href="signup_form.do">회원가입</a></p>
</body>
</html>