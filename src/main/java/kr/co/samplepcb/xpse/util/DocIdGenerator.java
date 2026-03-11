package kr.co.samplepcb.xpse.util;

import java.security.SecureRandom;
import java.util.Base64;

public class DocIdGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private DocIdGenerator() {
    }

    /**
     * ES 스타일 ID 생성 (20자, URL-safe Base64, 패딩 없음)
     * 15 bytes → Base64 = 20 chars
     */
    public static String generate() {
        byte[] bytes = new byte[15];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
