const BSCrypto = (() => {
    // --- 공통 유틸리티 ---
    const Utils = {
        bytesToBase64: (bytes) => {
            return btoa(String.fromCharCode.apply(null, new Uint8Array(bytes)));
        },
        getNonce: () => {
            const nonceEl = document.getElementById("bsNonce");
            if (!nonceEl || !nonceEl.value) {
                throw new Error("키 생성 중 오류 발생");
            }
            return nonceEl.value;
        },
        serializeForm: (form) => {
            const formData = new FormData(form);
            const params = new URLSearchParams();
            for (const [key, value] of formData.entries()) {
                if (key !== 'g' && key !== 'b') {
                    let processedValue = value;
                    if (typeof value === 'string') {
                        processedValue = value.replace(/</g, "&lt;").replace(/>/g, "&gt;");
                    }
                    params.append(key, processedValue);
                }
            }
            return params.toString();
        },
        xor: (dataBytes, keyBytes) => {
            const result = new Uint8Array(dataBytes.length);
            for (let i = 0; i < dataBytes.length; i++) {
                result[i] = dataBytes[i] ^ keyBytes[i % keyBytes.length];
            }
            return result;
        },
        encrypt: (plaintext, keys) => {
            const textEncoder = new TextEncoder();
            let dataBytes = textEncoder.encode(plaintext);
            let encrypted = Utils.xor(dataBytes, keys.key1);
            encrypted = Utils.xor(encrypted, keys.key2);
            return encrypted;
        }
    };

    const SubmitLogic = {
        SEEDS: {
            S1: "W15ed1IuVhdXRzURJl",
            S2: "MhRVVhoIUiwlWg==",
            S3: "ccCVZaUlh0IClQZ1xH",
            S4: "FZWV1kXEEQY1YGGH4A"
        },
        KEY_DERIVATION_STREAM: "BS.Crypto.Secure.Channel.Key.Stream",

        deriveKeys: () => {
            const nonce = Utils.getNonce();
            const combined = SubmitLogic.SEEDS.S1 + SubmitLogic.SEEDS.S2 + nonce + SubmitLogic.SEEDS.S3 + SubmitLogic.SEEDS.S4;
            const textEncoder = new TextEncoder();
            const initialSeed = textEncoder.encode(combined);
            const derivedStream = new Uint8Array(initialSeed.length);
            for (let i = 0; i < initialSeed.length; i++) {
                derivedStream[i] = initialSeed[i] ^ SubmitLogic.KEY_DERIVATION_STREAM.charCodeAt(i % SubmitLogic.KEY_DERIVATION_STREAM.length);
            }
            return {
                key1: derivedStream.slice(0, 16),
                key2: derivedStream.slice(16, 32),
                sid: derivedStream
            };
        }
    };
    const WriteLogic = {
        deriveKeys: async () => {
            const scriptTag = document.getElementById('bs-crypto-script');
            if (!scriptTag) throw new Error("Script tag with id 'bs-crypto-script' not found.");
            const nonce = Utils.getNonce();
            const response = await fetch(scriptTag.src + '?t=' + new Date().getTime());
            let sourceCode = await response.text();
            sourceCode = sourceCode.replace(/[^a-zA-Z0-9]/g, '');
            const textEncoder = new TextEncoder();
            const dataToHash = textEncoder.encode(sourceCode + nonce);
            const hashBuffer = await crypto.subtle.digest('SHA-256', dataToHash);
            const hashBytes = new Uint8Array(hashBuffer);
            return {
                key1: hashBytes.slice(0, 16),
                key2: hashBytes.slice(16, 32),
            };
        }
    };

    async function secureSubmit(form) {
        try {
            const mode = form.getAttribute('data-encrypt-mode') || 'submit';

            if (form.enctype === "multipart/form-data") {
                const hybridFormData = new FormData();
                const textParams = new URLSearchParams();
                const originalFormData = new FormData(form);

                for (const [key, value] of originalFormData.entries()) {
                    if (value instanceof File && value.size > 0) {
                        hybridFormData.append(key, value, value.name);
                    } else if (key !== "g" && key !== "b") {
                        let processedValue = value;
                        if (typeof value === 'string') {
                            processedValue = value.replace(/</g, "&lt;").replace(/>/g, "&gt;");
                        }
                        textParams.append(key, processedValue);
                    }
                }
                const posting_data = textParams.toString();
                if (posting_data) {
                    if (mode === 'submit') {
                        const keys = SubmitLogic.deriveKeys();
                        const encryptedBytes = Utils.encrypt(posting_data, keys);
                        hybridFormData.append("g", Utils.bytesToBase64(encryptedBytes));
                        hybridFormData.append("b", Utils.bytesToBase64(keys.sid.slice(0, 32)));
                    } else {
                        const keys = await WriteLogic.deriveKeys();
                        const encryptedBytes = Utils.encrypt(posting_data, keys);
                        hybridFormData.append("g", Utils.bytesToBase64(encryptedBytes));
                    }
                }

                fetch(form.action, { method: form.method, body: hybridFormData })
                    .then(response => {
                        if (response.ok) window.location.href = response.url;
                        else alert("요청 처리 실패");
                    }).catch(error => console.error("Fetch 전송 오류:", error));
                return;
            }

            const serializedData = Utils.serializeForm(form);

            const tempForm = document.createElement("form");
            tempForm.method = form.method;
            tempForm.action = form.action;
            tempForm.style.display = "none";
            
            document.body.appendChild(tempForm);

            try {
                if (mode === 'submit') {
                    const keys = SubmitLogic.deriveKeys();
                    const encryptedBytes = Utils.encrypt(serializedData, keys);
                    
                    const gInput = document.createElement("input");
                    gInput.type = "hidden";
                    gInput.name = "g";
                    gInput.value = Utils.bytesToBase64(encryptedBytes);
                    tempForm.appendChild(gInput);
    
                    const bInput = document.createElement("input");
                    bInput.type = "hidden";
                    bInput.name = "b";
                    bInput.value = Utils.bytesToBase64(keys.sid.slice(0, 32));
                    tempForm.appendChild(bInput);
    
                } else { // 'write'
                    const keys = await WriteLogic.deriveKeys();
                    const encryptedBytes = Utils.encrypt(serializedData, keys);
    
                    const gInput = document.createElement("input");
                    gInput.type = "hidden";
                    gInput.name = "g";
                    gInput.value = Utils.bytesToBase64(encryptedBytes);
                    tempForm.appendChild(gInput);
                }
    
                tempForm.submit();
            } finally {
                document.body.removeChild(tempForm);
            }

        } catch (e) {
            console.error("오류 발생:", e);
        }
    }
    function initialize() {
        document.querySelectorAll('form[data-encrypt="true"]').forEach((form) => {
            form.addEventListener("submit", function (event) {
                event.preventDefault();
                
                const confirmMsg = this.getAttribute("data-confirm-message");
                if (confirmMsg && !confirm(confirmMsg)) {
                  return;
                }
                secureSubmit(this);
            });
        });
    }
    
    return {
        initialize: initialize
    };

})();

document.addEventListener("DOMContentLoaded", BSCrypto.initialize);