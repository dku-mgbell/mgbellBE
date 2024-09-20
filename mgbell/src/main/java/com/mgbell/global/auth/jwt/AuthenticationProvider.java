package com.mgbell.global.auth.jwt;

import com.mgbell.user.model.entity.UserRole;

import java.util.Optional;

public interface AuthenticationProvider {
    String createAccessToken(String userId, UserRole userRole);
    String createRefreshToken();
    boolean isTokenValid(String token);

}
