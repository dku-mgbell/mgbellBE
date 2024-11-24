package com.mgbell.user.service;

import com.mgbell.global.nhn.service.NhnService;
import com.mgbell.user.exception.IncorrectEmailCode;
import com.mgbell.user.exception.OAuthUserHasNoPasswordException;
import com.mgbell.user.exception.UserAlreadyExistException;
import com.mgbell.user.exception.UserNotFoundException;
import com.mgbell.user.model.dto.response.TokenValidationResponse;
import com.mgbell.user.model.entity.user.User;
import com.mgbell.user.repository.TokenRedisRepository;
import com.mgbell.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final UserService userService;
    private final TokenRedisRepository tokenRedisRepository;
    private final NhnService nhnService;
    private final UserRepository userRepository;

    @Transactional
    public void sendVerificationCode(String email) {
        if(userService.isDuplicateEmail(email)){
            throw new UserAlreadyExistException();
        }

        String code = createRandomAccessCode();

        tokenRedisRepository.saveToken(email, code);
        log.info(code);
        nhnService.sendEmail(email, "마감벨 이메일 인증", "마감벨 이메일 인증 코드는 " + code + "입니다.");
    }

    public void sendVerificationCodeForPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        if (user.getPassword() == null) {
            throw new OAuthUserHasNoPasswordException();
        }

        String code = createRandomAccessCode();

        tokenRedisRepository.saveToken(email, code);
        log.info(code);
        nhnService.sendEmail(email, "마감벨 비밀번호 변경 이메일 인증", "마감벨 이메일 인증 코드는 " + code + "입니다.");
    }

    public TokenValidationResponse validateToken(String email, String validToken) {
        boolean isValid = tokenRedisRepository.getToken(email).equals(validToken);

        if(!isValid){
            log.info("error");
            throw new IncorrectEmailCode();
        }
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        return TokenValidationResponse.builder()
                .valid(tokenRedisRepository.getToken(email).equals(validToken))
                .signupToken(tokenRedisRepository.resetToken(email, uuid))
                .build();
    }

    public String createRandomAccessCode() {
        Random random = new Random();

        StringBuilder code = new StringBuilder();
        for(int i=0; i<6; i++) {
            int n = random.nextInt(36);
            if(n > 25) code.append(n-25);
            else {
                code.append((char) (n+65));
            }
        }

        return code.toString();
    }
}
