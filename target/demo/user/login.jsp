<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>로그인 페이지</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f8f9fa; display: flex; justify-content: center; align-items: center; height: 100vh; }
        .login-card { width: 100%; max-width: 400px; padding: 20px; background: white; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
    </style>
</head>
<body>

<div class="login-card">
    <h3 class="text-center mb-4">로그인</h3>
    <form action="loginAction.jsp" method="post">
        <div class="mb-3">
            <label class="form-label">아이디</label>
            <input type="text" name="userId" class="form-control" required>
        </div>
        <div class="mb-3">
            <label class="form-label">비밀번호</label>
            <input type="password" name="userPassword" class="form-control" required>
        </div>
        <button type="submit" class="btn btn-primary w-100">로그인</button>
    </form>
    <div class="text-center mt-3">
        <a href="register.jsp">회원가입 하러가기</a>
    </div>
</div>

</body>
</html>