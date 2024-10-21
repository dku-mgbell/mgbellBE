package com.mgbell.user.controller;

import com.mgbell.global.auth.jwt.JwtAuthentication;
import com.mgbell.global.config.swagger.AllUserAuth;
import com.mgbell.user.model.dto.request.OAuthSignupRequest;
import com.mgbell.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final UserService userService;

    @AllUserAuth
    @PatchMapping(path = "/signup")
    @Operation(summary = "OAuth2로 회원가입하기")
    public void signup(@RequestBody @Validated OAuthSignupRequest request, JwtAuthentication auth) {
        userService.oAuthSignup(request, auth.getUserId());
    }
}
