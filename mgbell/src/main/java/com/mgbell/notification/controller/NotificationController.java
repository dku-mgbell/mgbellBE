package com.mgbell.notification.controller;

import com.mgbell.global.auth.jwt.JwtAuthentication;
import com.mgbell.global.config.swagger.UserAuth;
import com.mgbell.notification.model.dto.request.NotificationRequest;
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

    @UserAuth
    @PostMapping("/register")
    @Operation(summary = "FCM 토큰 등록하기", description = "리액트에서 발급받은 토큰 입력")
    public void register(@RequestBody TokenRegisterRequest request, JwtAuthentication auth) {
        notificationService.register(request);
    }

    @UserAuth
    @PostMapping("/send")
    @Operation(summary = "백그라운드 알림 테스트")
    public void send(@RequestBody NotificationRequest request, JwtAuthentication auth) {
        User user = userRepository.findById(auth.getUserId())
                .orElseThrow(UserNotFoundException::new);

        notificationService.sendNotification(request);
    }
}
