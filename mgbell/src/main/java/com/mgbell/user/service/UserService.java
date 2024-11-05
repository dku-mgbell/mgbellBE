package com.mgbell.user.service;

import com.mgbell.global.auth.jwt.JwtProvider;
import com.mgbell.global.auth.jwt.JwtToken;
import com.mgbell.user.exception.IncorrectPassword;
import com.mgbell.user.exception.UserAlreadyExistException;
import com.mgbell.user.exception.UserNotFoundException;
import com.mgbell.user.model.dto.request.*;
import com.mgbell.user.model.dto.response.LoginResponse;
import com.mgbell.store.model.entity.Store;
import com.mgbell.user.model.dto.response.ReissueResponse;
import com.mgbell.user.model.dto.response.UserInfoResponse;
import com.mgbell.user.model.entity.user.User;
import com.mgbell.store.repository.StoreRepository;
import com.mgbell.user.repository.TokenRedisRepository;
import com.mgbell.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TokenRedisRepository tokenRedisRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StoreRepository storeRepository;

    public void signUp(String signupToken, SignupRequest request) {
        if(isDuplicateEmail(request.getEmail())) {
            throw new UserAlreadyExistException();
        }
        if(!tokenRedisRepository.getToken(request.getEmail()).equals(signupToken)) {
            throw new IncorrectPassword();
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

    public ReissueResponse reissue(ReissueRequest request) {
        JwtToken token = jwtProvider.reissue(request.getRefreshToken());

        return new ReissueResponse(
                token.getAccessToken(),
                token.getRefreshToken()
        );
    }

    public boolean isDuplicateEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public void oAuthSignup(OAuthSignupRequest request, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        user.setUserRole(request.getUserRole());
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
    }

    public String oAuthLogin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        return user.getEmail();
    }

    @Transactional
    public void edit(UserEditRequest request, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        Store store = user.getStore();

        if (store != null) {
            store.setPost(null);
            user.setStore(null);
        }
        userRepository.deleteById(id);
    }

    public void updatePwd(PasswordUpdateRequest request, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }

    public UserInfoResponse whoAmI(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        return new UserInfoResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
