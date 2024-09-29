package com.mgbell.user.controller;

import com.mgbell.global.auth.jwt.JwtAuthentication;
import com.mgbell.global.config.swagger.UserAuth;
import com.mgbell.user.model.dto.request.LoginRequest;
import com.mgbell.user.model.dto.request.SignupRequest;
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

    @UserAuth
    @GetMapping(path = "/oauth2/login")
    public String oAuthLogin(JwtAuthentication auth) {
        return userService.oAuthLogin(auth);
    }
}
