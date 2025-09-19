package com.paassible.boardservice.config;

import com.paassible.common.security.exception.CustomAuthenticationEntryPoint;
import com.paassible.common.security.jwt.JwtAuthenticationFilter;
import com.paassible.common.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 기본 보안 설정 OFF
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 세션 사용 안 함 (JWT 기반)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 인증 실패 시
                .exceptionHandling(
                        exceptionHandler ->
                                exceptionHandler.authenticationEntryPoint(
                                        customAuthenticationEntryPoint))

                // 인가 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/boards/swagger-ui/**",
                                "/boards/v3/api-docs/**",
                                "/boards/test",
                                "/boards/internal/**"
                        ).permitAll()
                        .requestMatchers("/boards/**").authenticated()
                        .anyRequest().permitAll()
                )

                // JWT 필터 등록
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtil),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
