package com.mgbell.user.service;

import com.mgbell.user.model.dto.request.LoginRequest;
import com.mgbell.user.model.dto.request.SignupRequest;
import com.mgbell.user.model.entity.User;
import com.mgbell.user.model.entity.UserRole;
import com.mgbell.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void signUp(SignupRequest request) {
        User user = User.builder()
                .userId(request.getUserId())
                .password(request.getPassword()) // Todo 비밀번호 암호화 기능
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .userRole(UserRole.USER)
                .build();

        userRepository.save(user);
    }

    public void login(LoginRequest request) {
        if(userRepository.findByUserId(request.getId()).isPresent()){
            /*
            Todo
             JWT 토큰 발급 로직 만들기
             */
        }
    }
}
