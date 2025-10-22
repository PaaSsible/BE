package com.paassible.common.security.jwt;

import com.paassible.common.security.token.RefreshTokenRepository;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    private final RefreshTokenRepository refreshTokenRepository;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Long userId, String username, Role role, boolean agreedToTerms) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role.name())
                .claim("username", username)
                .claim("agreedToTerms", agreedToTerms)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(key)
                .compact();
    }

    public String createIdToken(Long userId, Role role) {
        JwtBuilder builder =
                Jwts.builder()
                        .claim("userId", userId)
                        .claim("role", role.name())
                        .issuedAt(new Date())
                        .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                        .signWith(key);

        return builder.compact();
    }

    public String createRefreshToken(Long userId) {
        String refreshToken =
                Jwts.builder()
                        .subject(String.valueOf(userId))
                        .issuedAt(new Date())
                        .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                        .signWith(key)
                        .compact();

        refreshTokenRepository.save(userId, refreshToken, refreshTokenExpiration);

        return refreshToken;
    }

    public Long getUserId(String token) {
        String subject =
                Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getSubject();

        return Long.parseLong(subject);
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    public boolean getAgreedToTerms(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("agreedToTerms", Boolean.class);
    }

    public long getAccessTokenValidity() {
        return accessTokenExpiration;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setAttribute("SameSite", "None");

        return cookie;
    }
}