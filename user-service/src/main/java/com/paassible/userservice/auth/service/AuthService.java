package com.paassible.userservice.auth.service;

import com.paassible.common.response.ErrorCode;
import com.paassible.common.security.jwt.JwtUtil;
import com.paassible.common.security.jwt.Role;
import com.paassible.common.security.token.RefreshTokenRepository;
import com.paassible.userservice.auth.dto.AuthCodeRequest;
import com.paassible.userservice.auth.dto.TokenResponse;
import com.paassible.userservice.auth.exception.AuthException;
import com.paassible.userservice.auth.oauth.GoogleOAuthService;
import com.paassible.userservice.auth.oauth.GoogleUserInfo;
import com.paassible.userservice.user.entity.User;
import com.paassible.userservice.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final GoogleOAuthService googleOAuthService;

    public TokenResponse createAccessToken(AuthCodeRequest request, HttpServletResponse response) {

        GoogleUserInfo userInfo = googleOAuthService.processCode(request.getCode());

        User user = userService.findOrCreateUser(userInfo);

        Long userId = user.getId();
        Role role = user.getRole();
        boolean termsAgreed = user.isTermsAgreed();

        String newAccessToken = jwtUtil.createAccessToken(userId, role, termsAgreed);
        String newRefreshToken = jwtUtil.createRefreshToken(userId);

        response.addCookie(jwtUtil.createRefreshTokenCookie(newRefreshToken));

        long expiresAt = System.currentTimeMillis() + jwtUtil.getAccessTokenValidity();

        return new TokenResponse(newAccessToken, expiresAt);
    }

    public TokenResponse refreshAccessToken(
            HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new AuthException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        Long userId = jwtUtil.getUserId(refreshToken);
        String storedToken = refreshTokenRepository.get(userId);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new AuthException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        User user = userService.getUser(userId);
        Role role = user.getRole();
        boolean termsAgreed = user.isTermsAgreed();

        String newAccessToken = jwtUtil.createAccessToken(userId, role, termsAgreed);
        String newRefreshToken = jwtUtil.createRefreshToken(userId);

        response.addCookie(jwtUtil.createRefreshTokenCookie(newRefreshToken));

        long expiresAt = System.currentTimeMillis() + jwtUtil.getAccessTokenValidity();

        return new TokenResponse(newAccessToken, expiresAt);
    }

    public void logout(Long userId, HttpServletResponse response) {

        refreshTokenRepository.delete(userId);

        Cookie expiredCookie = new Cookie("refreshToken", null);
        expiredCookie.setHttpOnly(true);
        expiredCookie.setPath("/");
        expiredCookie.setMaxAge(0);

        response.addCookie(expiredCookie);
    }
}
