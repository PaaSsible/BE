package com.paassible.common.security.token;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void save(Long userId, String refreshToken, long refreshTokenExpiration) {
        redisTemplate
                .opsForValue()
                .set(
                        "refreshToken:" + userId,
                        refreshToken,
                        refreshTokenExpiration,
                        TimeUnit.MILLISECONDS);
    }

    public String get(Long userId) {
        return redisTemplate.opsForValue().get("refreshToken:" + userId.toString());
    }

    public void delete(Long userId) {
        redisTemplate.delete("refreshToken:" + userId.toString());
    }
}
