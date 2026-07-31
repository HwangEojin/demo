/**
 * 범용 취약점 트리거 피드백 모달 표시 함수
 */
function showFeedback({
  title = "공격 성공",
  message,
  type = "GENERAL",
  icon = null,
  triggerApi = null,
  executedData = null,
  resultData = null, // 이 줄은 이전 상태로 되돌릴 때 제거되어야 합니다.
  onClose = null,
}) {
  if (document.querySelector(".feedback-overlay")) {
    return false;
  }

  const vulnPreset = {
    XSS: { defaultIcon: "⚡", risk: "High" },
    SQLI: { defaultIcon: "💉", risk: "Critical" },
    IDOR: { defaultIcon: "🔓", risk: "High" },
    CSRF: { defaultIcon: "🌐", risk: "High" },
    FILE_UPLOAD: { defaultIcon: "📁", risk: "Critical" },
    PATH_TRAVERSAL: { defaultIcon: "📂", risk: "Critical" },
    GENERAL: { defaultIcon: "🎉", risk: "N/A" },
  };

  const config = vulnPreset[type.toUpperCase()] || vulnPreset["GENERAL"];
  const displayIcon = icon || config.defaultIcon;

  const overlay = document.createElement("div");
  overlay.className = "feedback-overlay";

  const toast = document.createElement("div");
  toast.className = "feedback-toast-large";

  const closeButton = document.createElement("button");
  closeButton.className = "feedback-close-btn";
  closeButton.innerHTML = "&times;";
  closeButton.setAttribute("aria-label", "닫기");

  const iconDiv = document.createElement("div");
  iconDiv.className = "feedback-icon";
  iconDiv.innerText = displayIcon;

  const content = document.createElement("div");
  content.className = "feedback-content";

  const titleElem = document.createElement("h2");
  titleElem.innerText = title;

  const metaContainer = document.createElement("div");
  metaContainer.className = "feedback-meta";

  const now = new Date();
  const timestamp = now.toLocaleTimeString("ko-KR", {
    hour12: false,
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  });
  const timeElem = document.createElement("span");
  timeElem.className = "feedback-timestamp";
  timeElem.innerText = `성공 시간: ${timestamp}`;

  const badgesContainer = document.createElement("div");
  badgesContainer.className = "feedback-badges";

  const typeBadge = document.createElement("span");
  typeBadge.className = "feedback-badge";
  typeBadge.innerText = type.toUpperCase();
  badgesContainer.appendChild(typeBadge);

  if (config.risk && config.risk !== "N/A") {
    const riskBadge = document.createElement("span");
    riskBadge.className = "feedback-badge risk-" + config.risk.toLowerCase();
    riskBadge.innerText = `위험도: ${config.risk}`;
    badgesContainer.appendChild(riskBadge);
  }

  const textElem = document.createElement("p");
  textElem.innerText = message;

  metaContainer.appendChild(badgesContainer);
  metaContainer.appendChild(timeElem);
  content.appendChild(metaContainer);
  content.appendChild(titleElem);
  content.appendChild(textElem);

  if (triggerApi || executedData) { // resultData 조건 제거
    const dataContainer = document.createElement("div");
    dataContainer.className = "feedback-data-container";

    if (triggerApi) {
      const apiLabel = document.createElement("div");
      apiLabel.className = "feedback-data-label";
      apiLabel.innerText = "Triggered API";
      const apiCode = document.createElement("pre");
      apiCode.innerText = triggerApi;
      dataContainer.appendChild(apiLabel);
      dataContainer.appendChild(apiCode);
    }

    if (executedData) {
      const resultLabel = document.createElement("div");
      resultLabel.className = "feedback-data-label";
      resultLabel.innerText = "Executed Data / Result";
      const resultCode = document.createElement("pre");
      resultCode.innerText = executedData ? executedData : "(Empty or Undefined)";
      dataContainer.appendChild(resultLabel);
      dataContainer.appendChild(resultCode);
    }
    content.appendChild(dataContainer);
  }

  toast.appendChild(closeButton);
  toast.appendChild(iconDiv);
  toast.appendChild(content);
  overlay.appendChild(toast);

  if (!document.getElementById("feedback-style")) {
    const style = document.createElement("style");
    style.id = "feedback-style";
    style.innerHTML = `
          .feedback-overlay {
              position: fixed; top: 0; left: 0; width: 100%; height: 100%;
              background-color: rgba(0, 0, 0, 0.75);
              display: flex; justify-content: center; align-items: center;
              z-index: 2000; opacity: 0;
              animation: feedback-fade-in 0.3s forwards;
          }
          .feedback-toast-large {
              position: relative;
              background: var(--card-bg-color, #ffffff);
              color: var(--main-text-color, #111111);
              padding: 40px 45px 35px 45px;
              border-radius: 8px;
              border: 1px solid var(--border-color, #e0e0e0);
              box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
              text-align: left; transform: scale(0.8); opacity: 0;
              animation: feedback-zoom-in 0.3s 0.1s forwards;
              display: flex; align-items: flex-start; gap: 25px; max-width: 750px; width: 90%;
          }
          .feedback-close-btn {
              position: absolute; top: 15px; right: 20px;
              background: none; border: none; font-size: 1.8rem;
              line-height: 1; color: #888888; cursor: pointer; padding: 0;
              transition: color 0.2s;
          }
          .feedback-close-btn:hover { color: #111111; }
          .feedback-icon { font-size: 3.5rem; line-height: 1; margin-top: 5px; }
          .feedback-meta { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 10px; margin-bottom: 12px;}
          .feedback-badges { display: flex; flex-wrap: wrap; gap: 8px; }
          .feedback-badge { display: inline-block; font-size: 0.75rem; font-weight: 700; padding: 3px 9px; border-radius: 4px; background-color: var(--main-text-color, #333); color: var(--card-bg-color, #ffffff); }
          .feedback-badge.risk-critical { background-color: #c0392b; }
          .feedback-badge.risk-high { background-color: #e67e22; }
          .feedback-timestamp { font-size: 0.8rem; color: var(--secondary-text-color, #555555); white-space: nowrap; }
          .feedback-content { width: 100%; }
          .feedback-content h2 { margin: 0 0 8px 0; color: var(--main-text-color, #111111); font-size: 1.8rem; font-weight: 700; }
          .feedback-content p { margin: 0 0 15px 0; font-size: 1rem; color: var(--secondary-text-color, #555555); line-height: 1.5; }
          .feedback-data-container { background-color: #1e1e1e; padding: 15px; border-radius: 6px; margin-top: 10px; width: 100%; box-sizing: border-box; }
          .feedback-data-label { font-size: 0.75rem; color: #aaaaaa; margin-bottom: 5px; font-weight: bold; text-transform: uppercase; }
          .feedback-data-container pre { font-family: monospace; font-size: 0.9rem; color: #4af626; margin: 0 0 15px 0; white-space: pre-wrap; word-wrap: break-word; }
          .feedback-data-container pre:last-child { margin-bottom: 0; }
          @keyframes feedback-fade-in { to { opacity: 1; } }
          @keyframes feedback-fade-out { to { opacity: 0; } }
          @keyframes feedback-zoom-in { to { transform: scale(1); opacity: 1; } }
      `;
    document.head.appendChild(style);
  }

  const mountModal = () => {
    if (document.body) {
      document.body.appendChild(overlay);
    }
  };

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", mountModal);
  } else {
    mountModal();
  }

  function closeFeedback() {
    document.removeEventListener("keydown", handleEscKey);
    overlay.style.animation = "feedback-fade-out 0.3s forwards";
    overlay.addEventListener("animationend", () => {
      if (overlay.parentNode) overlay.remove();
      if (typeof onClose === "function") onClose();
    });
  }

  function handleEscKey(e) {
    if (e.key === "Escape") closeFeedback();
  }

  closeButton.addEventListener("click", closeFeedback);
  overlay.addEventListener("click", (e) => {
    if (e.target === overlay) closeFeedback();
  });
  document.addEventListener("keydown", handleEscKey);

  return true;
}

function pollFeedbackStatus() {

  const contextPath = window.contextPath || "";
  //credentials 옵션 및 cache 옵션 추가
  fetch(`${contextPath}/api/feedback/status.do`, {
    method: "GET",
    credentials: "same-origin",
    cache: "no-store", 
  })
    .then((response) => {
      if (!response.ok) throw new Error("Network response was not ok");
      return response.json();
    })
    .then((data) => {
      if (data && data.hasFeedback) {
        if (typeof showFeedback === "function") {
          showFeedback({
            title: data.title,
            message: data.message,
            type: data.type,
            executedData: data.executedData || null,
          });
        }
      }
    })
    .catch((error) => {
      console.debug("Feedback polling failed:", error);
    })
    .finally(() => {
      setTimeout(pollFeedbackStatus, 3000); // 3초 주기 폴링
    });
}

// DOM 로드 즉시 최초 1회 즉시 실행
document.addEventListener("DOMContentLoaded", () => {
  pollFeedbackStatus();
});