package com.mgbell.user.controller;

import com.mgbell.user.model.dto.request.EmailRequest;
import com.mgbell.user.model.dto.request.TokenValidationRequest;
import com.mgbell.user.model.dto.response.TokenValidationResponse;
import com.mgbell.user.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/sendCode")
    @Operation(summary = "이메일 인증 코드 전송")
    public void sendVerificationCode(@RequestBody @Validated EmailRequest request) {
        emailService.sendVerificationCode(request.getEmail());
    }

    @PostMapping("/sendCode/password")
    @Operation(summary = "이메일 인증 코드 전송")
    public void sendVerificationCodeForPassword(@RequestBody @Validated EmailRequest request) {
        emailService.sendVerificationCode(request.getEmail());
    }

    @PostMapping("/verifyCode")
    @Operation(summary = "이메일 인증 코드 검증")
    public ResponseEntity<TokenValidationResponse> verifyCode(@RequestBody TokenValidationRequest request) {
        return ResponseEntity.ok(
                emailService.validateToken(
                        request.getEmail(),
                        request.getToken()
                )
        );
    }
}
