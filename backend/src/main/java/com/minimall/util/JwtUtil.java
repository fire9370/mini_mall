package com.minimall.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类：生成与解析 Token
 */
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expireMillis;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expire-hours}") long expireHours) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("未配置 JWT 密钥：请设置环境变量 JWT_SECRET（至少 32 字节），不要将真实密钥提交到仓库");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT 密钥太短：HS256 至少需要 32 字节，请设置更长的 JWT_SECRET");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expireMillis = expireHours * 3600_000L;
    }

    /**
     * 生成 Token
     *
     * @param id   用户ID 或 管理员ID
     * @param type user 或 admin
     */
    public String generateToken(Long id, String type) {
        return Jwts.builder()
                .subject(String.valueOf(id))
                .claim("type", type)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expireMillis))
                .signWith(key)
                .compact();
    }

    /**
     * 解析 Token 返回 Claims；非法或过期会抛异常
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
