package com.taehun.board.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TokenProvider {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private final Key key;

    @Value("${jwt.token-validity-in-seconds}")
    private long tokenValidity;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidity;

    public TokenProvider(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, boolean isRefreshToken) {
        Date now = new Date();
        long validity = isRefreshToken ? refreshTokenValidity : tokenValidity;
        Date expiryDate = new Date(now.getTime() + validity * 1000);

        log.debug("Generating JWT: username={}, isRefreshToken={}, expiryDate={}", username, isRefreshToken, expiryDate);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public void saveRefreshToken(String username, String refreshToken, long expiration) {
        redisTemplate.opsForValue().set("refreshToken:" + username, refreshToken, expiration, TimeUnit.SECONDS);
    }

    public void validateRefreshTokenOrThrow(String username, String refreshToken) {
        String storedToken = redisTemplate.opsForValue().get("refreshToken:" + username);

        if (storedToken == null || !refreshToken.equals(storedToken)) {
            throw new RuntimeException("Invalid Refresh Token.");
        }
    }

    public void blacklistAccessToken(String token, long expiration) {
        redisTemplate.opsForValue().set("blacklist:" + token, "true", expiration, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey("blacklist:" + token);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", token, e);
            throw e;
        } catch (JwtException e) {
            log.error("Invalid token: {}", token, e);
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Date getExpirationFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}
