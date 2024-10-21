package com.mgbell.user.controller;

import com.mgbell.global.auth.jwt.JwtAuthentication;
import com.mgbell.global.config.swagger.AllUserAuth;
import com.mgbell.user.model.dto.request.*;
import com.mgbell.user.model.dto.response.IdDupValidResponse;
import com.mgbell.user.model.dto.response.LoginResponse;
import com.mgbell.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "로그인")
    public ResponseEntity<LoginResponse> login(@RequestBody @Validated LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @AllUserAuth
    @PatchMapping(path = "/edit")
    @Operation(summary = "회원 정보 수정")
    public void edit(@RequestBody UserEditRequest request, JwtAuthentication auth) {
        userService.edit(request, auth.getUserId());
    }

    @AllUserAuth
    @DeleteMapping(path = "/delete")
    @Operation(summary = "회원 탈퇴")
    public void delete(JwtAuthentication auth) {
        userService.delete(auth.getUserId());
    }

    @AllUserAuth
    @PatchMapping(path = "/password")
    @Operation(summary = "비밀번호 재설정")
    public void updatePwd(@RequestBody PasswordUpdateRequest request, JwtAuthentication auth) {
        userService.updatePwd(request, auth.getUserId());
    }

    @PostMapping(path = "/signup")
    @Operation(summary = "회원가입")
    public void signup(@RequestBody @Validated SignupRequest request) {
        userService.signUp(request);
    }

    @PostMapping(path = "/dupCheck")
    @Operation(summary = "아이디 중복 검사 결과 반환(True: 중복됨, False: 중복 되지 않음)")
    public ResponseEntity<IdDupValidResponse> dupCheck(@RequestBody @Validated EmailRequest request) {
        boolean valid = userService.isDuplicateEmail(request.getEmail());
        IdDupValidResponse response = IdDupValidResponse.builder()
                .valid(valid)
                .build();
        return ResponseEntity.ok(response);
    }
}
