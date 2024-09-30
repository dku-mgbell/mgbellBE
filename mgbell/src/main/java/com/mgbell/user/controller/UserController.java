package com.mgbell.user.controller;

import com.mgbell.global.auth.jwt.JwtAuthentication;
import com.mgbell.global.config.swagger.UserAuth;
import com.mgbell.user.model.dto.request.EmailRequest;
import com.mgbell.user.model.dto.request.LoginRequest;
import com.mgbell.user.model.dto.request.SignupRequest;
import com.mgbell.user.model.dto.response.IdDupValidResponse;
import com.mgbell.user.model.dto.response.LoginResponse;
import com.mgbell.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(path = "/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Validated LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping(path = "/signup")
    public void signup(@RequestBody @Validated SignupRequest request) {
        userService.signUp(request);
    }

    @PostMapping(path = "/dupCheck")
    public ResponseEntity<IdDupValidResponse> dupCheck(@RequestBody @Validated EmailRequest request) {
        boolean valid = userService.isDuplicateEmail(request.getEmail());
        IdDupValidResponse response = IdDupValidResponse.builder()
                .valid(valid)
                .build();
        return ResponseEntity.ok(response);
    }
}
