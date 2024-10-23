package com.mgbell.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TokenRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveToken(String email, String token) {
        redisTemplate.opsForValue().set(email, token);
    }

    public String resetToken(String email, String token) {
        redisTemplate.opsForValue().setIfPresent(email, token);

        return token;
    }

    public String getToken(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    public void deleteToken(String email) {
        redisTemplate.delete(email);
    }

    public boolean hasKey(String email) {
        return redisTemplate.hasKey(email);
    }
}
