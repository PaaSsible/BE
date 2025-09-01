package com.paassible.userservice.auth.oauth;

import com.paassible.common.security.jwt.JwtUtil;
import com.paassible.common.security.jwt.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private static final String REDIRECT_URL = "http://localhost:8080/signin/callback";

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = oAuth2User.getId();
        Role role = oAuth2User.getRole();

        String accessToken = jwtUtil.createAccessToken(userId, role);
        String idToken = jwtUtil.createIdToken(userId, role);
        String refreshToken = jwtUtil.createRefreshToken(userId);

        response.addCookie(jwtUtil.createRefreshTokenCookie(refreshToken));

        String targetUrl =
                UriComponentsBuilder.fromUriString(REDIRECT_URL)
                        .queryParam("accessToken", accessToken)
                        //.queryParam("idToken", idToken)
                        .build()
                        .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
