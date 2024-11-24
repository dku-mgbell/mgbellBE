package com.mgbell.notification.repository;

import com.mgbell.notification.model.dto.request.TokenRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FcmRedisRepository {

    private final StringRedisTemplate tokenRedisTemplate;

    public void saveToken(TokenRegisterRequest request) {
        tokenRedisTemplate.opsForValue()
                .set(request.getEmail(), request.getToken());
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
