package com.mgbell.notification.controller;

import com.mgbell.global.auth.jwt.JwtAuthentication;
import com.mgbell.global.config.swagger.AdminAuth;
import com.mgbell.global.config.swagger.AllUserAuth;
import com.mgbell.notification.model.dto.request.NotificationRequest;
import com.mgbell.notification.model.dto.request.OfficialNotificationRequest;
import com.mgbell.notification.model.dto.request.TokenRegisterRequest;
import com.mgbell.notification.service.NotificationService;
import com.mgbell.user.exception.UserNotFoundException;
import com.mgbell.user.model.entity.user.User;
import com.mgbell.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @AllUserAuth
    @PostMapping("/register")
    @Operation(summary = "FCM 토큰 등록하기", description = "리액트에서 발급받은 토큰 입력")
    public void register(@RequestBody TokenRegisterRequest request, JwtAuthentication auth) {
        notificationService.register(auth.getUserId(), request);
    }

    @AllUserAuth
    @PostMapping("/send")
    @Operation(summary = "백그라운드 알림 테스트")
    public void send(@RequestBody NotificationRequest request, JwtAuthentication auth) {
        userRepository.findById(auth.getUserId())
                .orElseThrow(UserNotFoundException::new);

        notificationService.sendNotification(request);
    }

    @AdminAuth
    @PostMapping("/nofify")
    @Operation(summary = "사용자에게 공지 보내기")
    public void send(@RequestBody OfficialNotificationRequest request, JwtAuthentication auth) {
        userRepository.findById(auth.getUserId())
                .orElseThrow(UserNotFoundException::new);

        notificationService.sendOfficialNotification(request);
    }
}
