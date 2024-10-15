package com.mgbell.global.auth.oauth2;

import com.mgbell.global.auth.jwt.JwtProvider;
import com.mgbell.global.auth.jwt.JwtToken;
import com.mgbell.user.model.entity.user.User;
import com.mgbell.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    @Value("${oauth.uri}")
    private String URI;
    private final CustomOAuth2UserService oAuth2UserService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        User user = oAuth2UserService.getUser();

        // accessToken, refreshToken 발급
        JwtToken token = jwtProvider.issue(user);

        // 토큰 전달을 위한 redirect
        String redirectUrl = UriComponentsBuilder.fromUriString(URI)
                .queryParam("accessToken", token.getAccessToken())
                .build().toUriString();

        response.sendRedirect(redirectUrl);
    }
}
