package com.minimall.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JWT 工具类测试
 */
class JwtUtilTest {

    private static final String SECRET = "test-secret-key-0123456789-abcdefghijklmnopqrstuvwxyz-ABCDEF";

    private final JwtUtil jwtUtil = new JwtUtil(SECRET, 72);

    @Test
    @DisplayName("生成后能解析回原始信息")
    void generateAndParseRoundtrip() {
        String token = jwtUtil.generateToken(1L, "user");
        Claims claims = jwtUtil.parseToken(token);
        assertEquals("1", claims.getSubject());
        assertEquals("user", claims.get("type", String.class));
    }

    @Test
    @DisplayName("管理员类型 token 能解析回 admin")
    void adminTypeRoundtrip() {
        String token = jwtUtil.generateToken(2L, "admin");
        Claims claims = jwtUtil.parseToken(token);
        assertEquals("2", claims.getSubject());
        assertEquals("admin", claims.get("type", String.class));
    }

    @Test
    @DisplayName("篡改后的 token 解析应抛异常")
    void tamperedTokenThrows() {
        String token = jwtUtil.generateToken(1L, "user");
        assertThrows(JwtException.class, () -> jwtUtil.parseToken(token + "tamper"));
    }
}
