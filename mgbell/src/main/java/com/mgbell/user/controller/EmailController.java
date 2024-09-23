package com.mgbell.user.controller;

import com.mgbell.user.model.dto.request.EmailRequest;
import com.mgbell.user.model.dto.request.TokenValidationRequest;
import com.mgbell.user.model.dto.response.TokenValidationResponse;
import com.mgbell.user.service.EmailService;
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
    public void sendVerificationCode(@RequestBody @Validated EmailRequest request) {
        emailService.sendVerificationCode(request.getEmail());
    }

    @PostMapping("/verifyCode")
    public ResponseEntity<TokenValidationResponse> verifyCode(@RequestBody TokenValidationRequest request) {
        return ResponseEntity.ok(
                emailService.validateToken(
                        request.getEmail(),
                        request.getToken()
                )
        );
    }
}
