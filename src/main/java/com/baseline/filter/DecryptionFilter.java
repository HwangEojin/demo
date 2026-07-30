package com.baseline.filter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

public class DecryptionFilter implements Filter {

    private String scriptSourceCache = null;

    // bsSubmit 방식에 사용될 정적 시드
    private static final Map<String, String> SEEDS = new HashMap<>();
    static {
        SEEDS.put("S1", "W15ed1IuVhdXRzURJl");
        SEEDS.put("S2", "MhRVVhoIUiwlWg==");
        SEEDS.put("S3", "ccCVZaUlh0IClQZ1xH");
        SEEDS.put("S4", "FZWV1kXEEQY1YGGH4A");
    }
    private static final String KEY_DERIVATION_STREAM = "BS.Crypto.Secure.Channel.Key.Stream";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try (InputStream is = filterConfig.getServletContext().getResourceAsStream("/js/BSCrypto.js")) {
            if (is == null) {
                throw new ServletException("키 생성용 스크립트 파일을 찾을 수 없습니다: /js/BSCrypto.js");
            }
            try (Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name())) {
                String rawScript = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                this.scriptSourceCache = rawScript.replaceAll("[^a-zA-Z0-9]", "");
            }
        } catch (IOException e) {
            throw new ServletException("키 생성용 스크립트 파일을 읽는 데 실패했습니다.", e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        // Level 1(vulnerable) 플레이그라운드 경로는 암호화 필터를 적용하지 않습니다.
        if (requestURI.startsWith(httpRequest.getContextPath() + "/playground/level1/")) {
            chain.doFilter(request, response);
            return;
        }

        String contentType = httpRequest.getContentType();

        // Multipart 요청 처리
        if (contentType != null && contentType.startsWith("multipart/form-data")) {
            try {
                MultipartRequestWrapper wrappedRequest = new MultipartRequestWrapper(httpRequest, this);
                chain.doFilter(wrappedRequest, response);
            } catch (Exception e) {
                System.err.println("[DecryptionFilter] Multipart 복호화 실패. 원인: " + e.getClass().getSimpleName() + " - "
                        + e.getMessage());
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "올바르지 않은 암호화 요청 (multipart)");
            }
        } else {
            // 일반 Form 요청 처리
            String g = httpRequest.getParameter("g");
            String b = httpRequest.getParameter("b");

            if (g != null && !g.isEmpty()) {
                try {
                    String decryptedQueryString;
                    if (b != null && !b.isEmpty()) {
                        // 'b' 파라미터가 있으면 bsSubmit 방식으로 복호화
                        decryptedQueryString = decryptSubmit(g, httpRequest);
                    } else {
                        // 'b' 파라미터가 없으면 bsWrite 방식으로 복호화
                        decryptedQueryString = decryptWrite(g, httpRequest);
                    }

                    System.out.println("[Debug] 복호화된 데이터: " + decryptedQueryString);
                    CryptoRequestWrapper wrappedRequest = new CryptoRequestWrapper(httpRequest, decryptedQueryString);
                    chain.doFilter(wrappedRequest, response);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(
                            "[DecryptionFilter] 복호화 실패. 원인: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                    ((HttpServletResponse) response).sendError(HttpServletResponse.SC_BAD_REQUEST, "올바르지 않은 암호화 요청");
                }
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    // bsSubmit 방식 복호화 (정적 시드 + Nonce)
    public String decryptSubmit(String encryptedBase64, HttpServletRequest request) throws Exception {
        String nonce = getNonceFromSession(request);
        String combinedSeed = SEEDS.get("S1") + SEEDS.get("S2") + nonce + SEEDS.get("S3") + SEEDS.get("S4");
        byte[] initialSeed = combinedSeed.getBytes(StandardCharsets.UTF_8);

        Map<String, byte[]> keys = deriveKeysFromSeed(initialSeed);
        return decryptWithKeys(encryptedBase64, keys);
    }

    // bsWrite 방식 복호화
    public String decryptWrite(String encryptedBase64, HttpServletRequest request) throws Exception {
        String nonce = getNonceFromSession(request);
        String dataToHash = this.scriptSourceCache + nonce;

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));

        Map<String, byte[]> keys = new HashMap<>();
        keys.put("key1", Arrays.copyOfRange(hashBytes, 0, 16));
        keys.put("key2", Arrays.copyOfRange(hashBytes, 16, 32));

        return decryptWithKeys(encryptedBase64, keys);
    }

    private String getNonceFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null)
            throw new IllegalStateException("세션이 존재하지 않아 Nonce를 가져올 수 없습니다.");

        String nonce = (String) session.getAttribute("bsNonce");
        if (nonce == null || nonce.isEmpty())
            throw new IllegalStateException("세션에 Nonce 값이 없습니다. 키를 생성할 수 없습니다.");

        return nonce;
    }

    private Map<String, byte[]> deriveKeysFromSeed(byte[] initialSeed) {
        byte[] derivedStream = new byte[initialSeed.length];
        byte[] derivationKeyBytes = KEY_DERIVATION_STREAM.getBytes(StandardCharsets.UTF_8);

        for (int i = 0; i < initialSeed.length; i++) {
            derivedStream[i] = (byte) (initialSeed[i] ^ derivationKeyBytes[i % derivationKeyBytes.length]);
        }

        Map<String, byte[]> keys = new HashMap<>();
        keys.put("key1", Arrays.copyOfRange(derivedStream, 0, 16));
        keys.put("key2", Arrays.copyOfRange(derivedStream, 16, 32));
        return keys;
    }

    private String decryptWithKeys(String encryptedBase64, Map<String, byte[]> keys) throws Exception {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);
        // 암호화의 역순으로 복호화 (XOR은 순서가 동일)
        byte[] decrypted = xor(xor(encryptedBytes, keys.get("key2")), keys.get("key1"));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    private byte[] xor(byte[] data, byte[] key) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ key[i % key.length]);
        }
        return result;
    }

    /**
     * 일반 폼 요청을 위한 Wrapper. 복호화된 쿼리 스트링 파라미터를 파싱하여 제공합니다.
     */
    public static class CryptoRequestWrapper extends HttpServletRequestWrapper {
        private final Map<String, String[]> paramMap = new HashMap<>();

        public CryptoRequestWrapper(HttpServletRequest request, String decryptedQueryString) {
            super(request);
            // 기존 원본 파라미터 복사 후 g, b 제거
            Map<String, String[]> origMap = request.getParameterMap();
            if (origMap != null) {
                for (Map.Entry<String, String[]> entry : origMap.entrySet()) {
                    if (!"g".equals(entry.getKey()) && !"b".equals(entry.getKey())) {
                        this.paramMap.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            parseQueryString(decryptedQueryString);
        }

        private void parseQueryString(String queryString) {
            if (queryString == null || queryString.isEmpty())
                return;
            for (String pair : queryString.split("&")) {
                int idx = pair.indexOf("=");
                if (idx > 0) {
                    try {
                        String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                        String value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
                        this.paramMap.put(key, new String[] { value });
                    } catch (Exception e) {
                        /* Malformed pair */ }
                }
            }
        }

        @Override
        public String getParameter(String name) {
            if (this.paramMap.containsKey(name)) {
                return this.paramMap.get(name)[0];
            }
            return super.getParameter(name);
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return Collections.unmodifiableMap(this.paramMap);
        }
    }

    /**
     * Multipart 요청을 위한 Wrapper. 'g' 파라미터를 복호화하고 파일 파트는 유지합니다.
     */
    public static class MultipartRequestWrapper extends HttpServletRequestWrapper {
        private final Map<String, String[]> paramMap = new HashMap<>();

        public MultipartRequestWrapper(HttpServletRequest request, DecryptionFilter filter)
                throws IOException, ServletException {
            super(request);

            Part gPart = request.getPart("g");
            if (gPart != null) {
                try (InputStream is = gPart.getInputStream();
                        Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name())) {

                    String encryptedData = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";

                    // [추가] 브라우저 및 전송 규격에 따라 삽입될 수 있는 후행 개행문자(\r\n) 제거
                    encryptedData = encryptedData.trim();

                    if (!encryptedData.isEmpty()) {
                        String decryptedQueryString;
                        Part bPart = request.getPart("b");
                        if (bPart != null) {
                            decryptedQueryString = filter.decryptSubmit(encryptedData, request);
                        } else {
                            decryptedQueryString = filter.decryptWrite(encryptedData, request);
                        }
                        System.out.println("[Debug] Multipart 복호화된 데이터: " + decryptedQueryString);
                        parseQueryString(decryptedQueryString);
                    }
                } catch (Exception e) {
                    throw new ServletException("Multipart 파라미터 'g' 복호화 실패", e);
                }
            }
        }

        private void parseQueryString(String queryString) {
            if (queryString == null || queryString.isEmpty())
                return;
            for (String pair : queryString.split("&")) {
                int idx = pair.indexOf("=");
                if (idx > 0) {
                    try {
                        String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                        String value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
                        this.paramMap.put(key, new String[] { value });
                    } catch (Exception e) {
                        /* Malformed pair */ }
                }
            }
        }

        @Override
        public String getParameter(String name) {
            if (this.paramMap.containsKey(name)) {
                return this.paramMap.get(name)[0];
            }
            if ("g".equals(name) || "b".equals(name)) {
                return null;
            }
            return super.getParameter(name);
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> originalMap = new HashMap<>(super.getParameterMap());
            originalMap.remove("g");
            originalMap.remove("b");
            originalMap.putAll(this.paramMap);
            return Collections.unmodifiableMap(originalMap);
        }
    }
}