<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<style>
    /* This style is duplicated from passwordCheckModal.jsp for modularity. */
    /* Consider moving to a common CSS file if more modals are added. */
    #withdrawalModal .modal-content {
        background: var(--main-bg-color, #ffffff);
        padding: 30px;
        border-radius: 8px;
        width: 100%;
        max-width: 420px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
        border: 1px solid var(--border-color, #e0e0e0);
    }
    #withdrawalModal .modal-content h4 {
        margin-top: 0;
        margin-bottom: 12px;
        font-size: 1.5rem;
        font-weight: 700;
        color: var(--main-text-color, #121212);
        text-align: center;
    }
    #withdrawalModal .modal-content p {
        font-size: 0.95rem;
        color: var(--secondary-text-color, #555);
        margin-bottom: 20px;
        text-align: center;
        line-height: 1.4;
    }
    #withdrawalModal .form-actions {
        display: flex;
        gap: 10px;
        margin-top: 20px;
    }
    #withdrawalModal .form-actions .btn {
        flex: 1;
    }
    #withdrawalModal .btn-danger {
        background-color: #d9534f;
        color: white;
    }
    #withdrawalModal .btn-danger:hover {
        background-color: #c9302c;
    }
    #withdrawalModal .btn-secondary {
        background-color: #6c757d;
        color: white;
    }
    #withdrawalModal .btn-secondary:hover {
        background-color: #5a6268;
    }
</style>

<div id="withdrawalModal" class="modal-overlay">
    <div class="modal-content">
        <h4>회원 탈퇴</h4>
        <p>계정을 영구적으로 삭제합니다.<br>탈퇴하시려면 현재 비밀번호를 입력해주세요.</p>
        <form id="withdrawalForm" onsubmit="return false;">
            <div class="form-group">
                <label for="withdrawalPw">현재 비밀번호</label>
                <input type="password" id="withdrawalPw" name="userPw" required placeholder="비밀번호를 입력하세요">
            </div>
            <p id="withdrawalErrorMessage" class="error-message" style="display:none;"></p>
            <div class="form-actions">
                <button type="button" id="cancelWithdrawalBtn" class="btn btn-secondary">취소</button>
                <button type="submit" class="btn btn-danger">탈퇴하기</button>
            </div>
        </form>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', function() {
    const withdrawalModal = document.getElementById('withdrawalModal');
    const showBtn = document.getElementById('withdrawalBtn');
    const cancelBtn = document.getElementById('cancelWithdrawalBtn');
    const withdrawalForm = document.getElementById('withdrawalForm');
    const withdrawalErrorMessage = document.getElementById('withdrawalErrorMessage');
    const passwordInput = document.getElementById('withdrawalPw');

    if (showBtn) {
        showBtn.addEventListener('click', function() {
            withdrawalModal.style.display = 'flex';
            passwordInput.focus();
        });
    }

    if (cancelBtn) {
        cancelBtn.addEventListener('click', function() {
            withdrawalModal.style.display = 'none';
            passwordInput.value = '';
            withdrawalErrorMessage.style.display = 'none';
        });
    }

    if (withdrawalForm) {
        withdrawalForm.addEventListener('submit', function(e) {
            e.preventDefault();
            withdrawalErrorMessage.style.display = 'none';
            const password = passwordInput.value;

            if (!confirm('정말로 회원에서 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) {
                return;
            }

            fetch('${pageContext.request.contextPath}/api/withdrawal.do', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: 'userPw=' + encodeURIComponent(password)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('회원 탈퇴가 완료되었습니다. 이용해주셔서 감사합니다.');
                    window.location.href = '${pageContext.request.contextPath}/';
                } else {
                    withdrawalErrorMessage.textContent = data.message || '비밀번호가 일치하지 않습니다.';
                    withdrawalErrorMessage.style.display = 'block';
                }
            })
            .catch(error => {
                console.error('Error:', error);
                withdrawalErrorMessage.textContent = '서버와 통신 중 오류가 발생했습니다.';
                withdrawalErrorMessage.style.display = 'block';
            });
        });
    }
});
</script>