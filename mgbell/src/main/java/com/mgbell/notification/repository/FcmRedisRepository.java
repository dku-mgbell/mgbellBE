package com.mgbell.notification.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FcmRedisRepository {

    private final StringRedisTemplate tokenRedisTemplate;

    public void saveToken(String email, String token) {
        tokenRedisTemplate.opsForValue()
                .set(email, token);
    }

    public String getToken(String studentId) {
        return tokenRedisTemplate.opsForValue().get(studentId);
    }

    public void deleteToken(String studentId) {
        tokenRedisTemplate.delete(studentId);
    }

    public boolean hasKey(String studentId) {
        return tokenRedisTemplate.hasKey(studentId);
    }
}
