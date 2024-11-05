package com.mgbell.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgbell.global.auth.jwt.JwtAuthEntryPoint;
import com.mgbell.global.auth.jwt.JwtAuthenticationFilter;
import com.mgbell.global.auth.jwt.JwtProvider;
import com.mgbell.global.auth.oauth2.CustomOAuth2UserService;
import com.mgbell.global.auth.oauth2.OAuth2SuccessHandler;
import com.mgbell.global.error.ErrorHandleFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(60);
        messageSource.setDefaultLocale(Locale.KOREA);
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    @Bean(name = "messageSourceAccessor")
    public MessageSourceAccessor messageSourceAccessor(MessageSource messageSource) {
        return new MessageSourceAccessor(messageSource, Locale.getDefault());
    }

    @Bean
    public LocaleResolver localeResolver() {
        CustomLocaleResolver customLocaleResolver = new CustomLocaleResolver();
        customLocaleResolver.setDefaultLocale(Locale.KOREA);
        return customLocaleResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider);
    }

    @Bean
    public JwtAuthEntryPoint jwtAuthEntryPoint() {
        return new JwtAuthEntryPoint();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ErrorHandleFilter errorHandleFilter() {
        return new ErrorHandleFilter(objectMapper, messageSource());
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           HandlerMappingIntrospector introspector, ErrorHandleFilter errorHandleFilter) throws Exception {
        return http
                .cors(httpSecurityCorsConfigurer -> corsConfigurationSource())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> {
                    authorizeRequests.requestMatchers(
                            new MvcRequestMatcher(introspector, "/swagger-ui/**")).permitAll();
                    authorizeRequests.requestMatchers("/v3/api-docs/**",
                            "/webjars/**", "/error", "/favicon.ico").permitAll();
                    authorizeRequests.requestMatchers("/user/**").permitAll();
                    authorizeRequests.requestMatchers("/store/**").permitAll();
                    authorizeRequests.requestMatchers("/post/**").permitAll();
                    authorizeRequests.requestMatchers("/email/**").permitAll();
                    authorizeRequests.requestMatchers("/oauth/**").permitAll();
                    authorizeRequests.requestMatchers("/order/**").permitAll();
                    authorizeRequests.requestMatchers("/favorite/**").permitAll();
                    authorizeRequests.requestMatchers("/review/**").permitAll();
                    authorizeRequests.requestMatchers("/login/**").permitAll();
                })
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(errorHandleFilter(), JwtAuthenticationFilter.class)
                .oauth2Login(oauth ->
                        oauth.userInfoEndpoint(c -> c.userService(oAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                )
                .build();
    }

    public static class CustomLocaleResolver extends AcceptHeaderLocaleResolver {

        String[] mLanguageCode = new String[]{"ko", "en"};
        List<Locale> mLocales = Arrays.asList(new Locale("en"), new Locale("es"), new Locale("ko"));

        @Override
        public Locale resolveLocale(HttpServletRequest request) {
            // 언어팩 변경
            String acceptLanguage = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
            if (acceptLanguage == null || acceptLanguage.isEmpty()) {
                return Locale.getDefault();
            }
            List<Locale.LanguageRange> list = Locale.LanguageRange.parse(
                    request.getHeader("Accept-Language"));

            mLocales = new ArrayList<>();
            for (String code : mLanguageCode) {
                mLocales.add(new Locale(code));
            }
            return Locale.lookup(list, mLocales);
        }
    }
}
