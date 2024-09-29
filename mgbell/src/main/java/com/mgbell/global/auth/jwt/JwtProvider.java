package com.mgbell.global.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgbell.user.model.entity.user.User;
import com.mgbell.user.model.entity.user.UserRole;
import com.mgbell.user.repository.UserRepository;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider implements AuthenticationProvider {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.expiration}")
    private long accessTokenValidityInSeconds;
    @Value("${jwt.refresh.expiration}")
    private long refreshTokenValidityInSeconds;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String USERNAME_CLAIM = "email";
    private static final String BEARER = "bearer ";
    public static final String AUTHORIZATION = "Authorization";

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public JwtToken issue(User user){
        return JwtToken.builder()
                .accessToken(createAccessToken(user.getId().toString(), user.getUserRole()))
                .refreshToken(createRefreshToken())
                .build();
    }

    public JwtAuthentication getAuthentication(String accessToken) {
        Jws<Claims> claimsJws = validateAccessToken(accessToken);

        Claims body = claimsJws.getBody();
        log.info(body.get("userId").toString());
        Long userId = Long.parseLong((String) body.get("userId"));
        UserRole userRole = UserRole.of((String) body.get("userRole"));

        return new JwtAuthentication(userId, userRole);
    }

    @Override
    public String createAccessToken(String userId, UserRole userRole) {
        Claims claims = Jwts.claims();
        claims.put("userId", userId);
        claims.put("userRole", userRole);

        return Jwts.builder()
                .setSubject(ACCESS_TOKEN_SUBJECT)
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidityInSeconds * 1000))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    @Override
    public String createRefreshToken() {
        return Jwts
                .builder()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidityInSeconds * 1000))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("TOKEN EXPIRED");
        } catch (JwtException e) {
            throw new RuntimeException("TOKEN INVALID");
        }
    }

    public Jws<Claims> validateAccessToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("TOKEN EXPIRED");
        } catch (JwtException e) {
            throw new RuntimeException("TOKEN INVALID");
        }
    }

    public String getAccessTokenFromHeader(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase("access-token")) {
                    return cookie.getValue();
                }
            }
        }

        String header = request.getHeader(AUTHORIZATION);
        if (header != null) {
            if (!header.toLowerCase().startsWith(BEARER)) {
                throw new RuntimeException();
            }
            return header.substring(7);
        }

        return null;
    }
}
