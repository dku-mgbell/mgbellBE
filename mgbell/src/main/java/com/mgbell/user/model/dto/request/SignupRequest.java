package com.mgbell.user.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @NotBlank(message = "아이디는 필수 입력값입니다")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]{5,12}$",
    message = "아이디의 첫 글자는 영어 대/소문자로 시작해야 하며, 영문, 숫자, '_'으로만 이루어진 5 ~ 12자 이하여야 합니다.")
    private String userId;

    @NotBlank(message = "이메일은 필수 입력값입니다")
    @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$",
    message = "이메일 형식이 올바르지 않습니다.")
    // OWASP Regex Repository
    // 1. 이메일 주소의 로컬 부분(local part). 영문 대소문자, 숫자 및 특수 문자 (+, -, _, &)로 구성
    // 2. 옵션으로, 점(.)으로 구분된 추가적인 로컬 부분을 허용
    // 3. 이메일 주소의 도메인 부분. 하나 이상의 하이픈, 영문 대소문자, 숫자로 구성된 문자열을 허용하며, 여러 개의 도메인 세그먼트를 허용
    // 4. 최상위 도메인. 두 글자에서 일곱 글자 사이의 영문 대소문자로 된 문자열을 허용
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=\\S+$).{8,16}",
            message = "비밀번호는 영문 대/소문자, 숫자, 특수문자를 포함하여 8~16자여야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력값입니다")
    @Pattern(regexp = "^[가-힣]*$", message = "이름을 정확히 입력해주세요")
    private String name;

    @NotBlank(message = "전화번호는 필수 입력값입니다")
    @Pattern(regexp = "^010[0-9]{4}[0-9]{4}$", message = "전화번호를 정확히 입력해주세요")
    private String phoneNumber;
}
