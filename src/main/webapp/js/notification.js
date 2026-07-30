document.addEventListener('DOMContentLoaded', function() {
    const container = document.querySelector('.notification-container');
    if (container) {
        container.addEventListener('click', function(e) {
            if (e.target.classList.contains('close-btn')) {
                const notification = e.target.closest('.notification');
                if (notification) {
                    notification.style.animation = 'slideUp 0.5s forwards';
                    notification.addEventListener('animationend', () => {
                        notification.remove();
                        if (container.children.length === 0) {
                            container.remove();
                        }
                    });
                }
            }
        });

        // Automatically remove notifications after a delay
        const notifications = container.querySelectorAll('.notification');
        notifications.forEach(notification => {
            setTimeout(() => {
                const closeBtn = notification.querySelector('.close-btn');
                if (closeBtn) closeBtn.click();
            }, 5000); // 5초 후 자동 닫힘
        });
    }
});
