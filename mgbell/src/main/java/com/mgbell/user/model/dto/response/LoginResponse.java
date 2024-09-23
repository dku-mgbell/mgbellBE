package com.mgbell.user.model.dto.response;

import com.mgbell.user.model.entity.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private UserRole role;
}
