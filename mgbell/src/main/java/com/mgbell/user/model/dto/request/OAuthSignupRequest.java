package com.mgbell.user.model.dto.request;

import com.mgbell.user.model.entity.user.UserRole;
import com.mgbell.user.model.entity.user.validator.Enum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OAuthSignupRequest {

    @Enum(enumClass = UserRole.class, message = "가입 유형은 필수 입력값입니다.(USER, OWNER)")
    private UserRole userRole;

    @NotBlank(message = "이름은 필수 입력값입니다")
    @Pattern(regexp = "^[가-힣]*$", message = "이름을 정확히 입력해주세요")
    private String name;

    @NotBlank(message = "전화번호는 필수 입력값입니다")
    @Pattern(regexp = "^010[0-9]{4}[0-9]{4}$", message = "전화번호를 정확히 입력해주세요")
    private String phoneNumber;
}
