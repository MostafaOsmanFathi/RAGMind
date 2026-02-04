package com.ragmind.ragbackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.Duration;

@Service
public class TokenBlockService {

    private final StringRedisTemplate redisTemplate;
    private final JwtService jwtService;
    private static final String blockAccessTokenRedisKey = "auth:blocked:access-token:";
    private static final String blockRefreshTokenRedisKey = "auth:blocked:refresh-token:";

    @Value("${jwt.access-token-validity}")
    private long jwtAccessTokenDuration;

    @Value("${jwt.refresh-token-validity}")
    private long jwtRefreshTokenDuration;

    public TokenBlockService(StringRedisTemplate redisTemplate, JwtService jwtService) {
        this.redisTemplate = redisTemplate;
        this.jwtService = jwtService;
    }

    public void blockToken(String token) {
        String key;
        Duration duration;
        if (jwtService.isRefreshToken(token)) {
            key = blockRefreshTokenRedisKey + token;
            duration = Duration.ofMillis(jwtRefreshTokenDuration);
        } else if (jwtService.isAccessToken(token)) {
            key = blockAccessTokenRedisKey + token;
            duration = Duration.ofMillis(jwtAccessTokenDuration);
        } else {
            return;
        }

        key = hash(key);

        redisTemplate.opsForValue().set(key, "1", duration);
    }

    public boolean checkTokenBlocked(String token) {
        String key;
        if (jwtService.isRefreshToken(token)) {
            key = blockRefreshTokenRedisKey + token;
        } else if (jwtService.isAccessToken(token)) {
            key = blockAccessTokenRedisKey + token;
        } else {
            return false;
        }

        key = hash(key);

        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void unblockToken(String token) {
        String key;
        if (jwtService.isRefreshToken(token)) {
            key = blockRefreshTokenRedisKey + token;
        } else if (jwtService.isAccessToken(token)) {
            key = blockAccessTokenRedisKey + token;
        } else {
            return;
        }

        key = hash(key);

        redisTemplate.delete(key);
    }

    private String hash(String token) {
        return DigestUtils.md5DigestAsHex(token.getBytes());
    }
}
