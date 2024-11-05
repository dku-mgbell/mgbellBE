package com.mgbell.user.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReissueResponse {
    private String accessToken;
    private String refreshToken;
}
