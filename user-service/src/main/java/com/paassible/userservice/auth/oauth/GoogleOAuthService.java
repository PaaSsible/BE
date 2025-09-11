package com.paassible.userservice.auth.oauth;

import com.paassible.common.response.ErrorCode;
import com.paassible.userservice.auth.exception.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleOAuthService {

    private final WebClient webClient = WebClient.create();

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.provider.google.token-uri}")
    private String tokenUri;

    @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
    private String userInfoUri;

    public GoogleUserInfo processCode(String code) {
        String decodedCode = URLDecoder.decode(code, StandardCharsets.UTF_8);
        String accessToken = getAccessToken(decodedCode);
        return getUserInfo(accessToken);
    }

    private String getAccessToken(String code) {
        GoogleTokenResponse tokenResponse = webClient.post()
                .uri(tokenUri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("code", code)
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("redirect_uri", redirectUri))
                .retrieve()
                .bodyToMono(GoogleTokenResponse.class)
                .block();

        return Optional.ofNullable(tokenResponse)
                .map(GoogleTokenResponse::getAccessToken)
                .orElseThrow(() -> new AuthException(ErrorCode.GOOGLE_TOKEN_EMPTY));
    }

    private GoogleUserInfo getUserInfo(String accessToken) {
        return webClient.get()
                .uri(userInfoUri)
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(GoogleUserInfo.class)
                .block();
    }
}