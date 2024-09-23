package com.mgbell.user.service;

import com.mgbell.global.auth.jwt.JwtProvider;
import com.mgbell.global.auth.jwt.JwtToken;
import com.mgbell.user.exception.IncorrectPassword;
import com.mgbell.user.exception.UserAlreadyExistException;
import com.mgbell.user.exception.UserNotFoundException;
import com.mgbell.user.model.dto.request.LoginRequest;
import com.mgbell.user.model.dto.request.SignupRequest;
import com.mgbell.user.model.dto.response.LoginResponse;
import com.mgbell.user.model.entity.user.User;
import com.mgbell.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public void signUp(SignupRequest request) {
        if(isDuplicateEmail(request.getEmail())) {
            throw new UserAlreadyExistException();
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .userRole(request.getUserRole())
                .build();

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(UserNotFoundException::new);

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            JwtToken token = jwtProvider.issue(user);
            return new LoginResponse(token.getAccessToken(), token.getRefreshToken(), user.getUserRole());
        } else {
            throw new IncorrectPassword();
        }
    }

    public Boolean isDuplicateEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
