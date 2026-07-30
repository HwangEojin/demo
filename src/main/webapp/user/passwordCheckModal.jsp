<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<style>
    .modal-overlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.6);
        display: none; /* 초기에는 숨김 */
        align-items: center;
        justify-content: center;
        z-index: 1000;
    }
    .modal-content {
        background: var(--main-bg-color, #ffffff);
        padding: 30px;
        border-radius: 8px;
        width: 100%;
        max-width: 400px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
        border: 1px solid var(--border-color, #e0e0e0);
    }
    .modal-content h4 {
        margin-top: 0;
        margin-bottom: 12px;
        font-size: 1.5rem;
        font-weight: 700;
        color: var(--main-text-color, #121212);
        text-align: center;
    }
    .modal-content p {
        font-size: 0.95rem;
        color: var(--secondary-text-color, #555);
        margin-bottom: 20px;
        text-align: center;
        line-height: 1.4;
    }
    .modal-content .form-group {
        margin-bottom: 20px;
    }
    .modal-content .form-group label {
        display: block;
        margin-bottom: 8px;
        font-weight: 500;
        font-size: 0.95rem;
        color: var(--main-text-color, #121212);
    }
    .modal-content .form-group input {
        width: 100%;
        padding: 12px;
        border: 1px solid var(--border-color, #e0e0e0);
        border-radius: 4px;
        font-size: 1rem;
        font-family: 'Noto Sans KR', sans-serif;
        outline: none;
        transition: border-color 0.3s;
    }
    .modal-content .form-group input:focus {
        border-color: var(--accent-color, #333);
    }
    .modal-content .error-message {
        color: #d93025;
        font-size: 0.9rem;
        margin-bottom: 15px;
        text-align: center;
    }
    .modal-content .form-actions {
        text-align: center;
    }
    .modal-content .btn {
        display: inline-block;
        width: 100%;
        padding: 12px;
        border: none;
        border-radius: 4px;
        background-color: var(--accent-color, #333);
        color: var(--accent-text-color, #ffffff);
        font-size: 1rem;
        font-weight: 500;
        text-align: center;
        cursor: pointer;
        transition: background-color 0.3s;
    }
    .modal-content .btn:hover {
        background-color: #000;
    }
</style>

<div id="passwordCheckModal" class="modal-overlay">
    <div class="modal-content">
        <h4>비밀번호 확인</h4>
        <p>마이페이지에 접근하려면<br> 현재 비밀번호를 입력해주세요.</p>
        <form id="passwordCheckForm" onsubmit="return false;">
            <div class="form-group">
                <label for="currentPw">현재 비밀번호</label>
                <input type="password" id="currentPw" name="userPw" required placeholder="비밀번호를 입력하세요">
            </div>
            <p id="modalErrorMessage" class="error-message" style="display:none;"></p>
            <div class="form-actions">
                <button type="submit" class="btn">확인</button>
            </div>
        </form>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById('passwordCheckModal');
    const form = document.getElementById('passwordCheckForm');
    const errorMessage = document.getElementById('modalErrorMessage');

    // 페이지 로드 시 모달 표시
    modal.style.display = 'flex';

    form.addEventListener('submit', function(e) {
        e.preventDefault();
        errorMessage.style.display = 'none';
        const password = document.getElementById('currentPw').value;

        fetch('${pageContext.request.contextPath}/api/verifyPassword.do', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'userPw=' + encodeURIComponent(password)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                modal.style.display = 'none'; // 성공 시 모달 숨김
            } else {
                errorMessage.textContent = data.message || '비밀번호가 일치하지 않습니다.';
                errorMessage.style.display = 'block';
            }
        })
        .catch(error => {
            console.error('Error:', error);
            errorMessage.textContent = '서버와 통신 중 오류가 발생했습니다.';
            errorMessage.style.display = 'block';
        });
    });
});
</script>