package com.paassible.common.security.jwt;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class CustomBasicAuthFilter extends OncePerRequestFilter {

    private static final String VALID_USERNAME = "root";
    private static final String VALID_PASSWORD = "1234";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String xBasicAuth = request.getHeader("X-Basic-Auth");

        if (xBasicAuth == null || xBasicAuth.isBlank()) {
            throw new CustomException(ErrorCode.BASIC_AUTH_FAILED);
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(xBasicAuth);
            String decodedString = new String(decoded, StandardCharsets.UTF_8);

            String[] parts = decodedString.split(":", 2);
            if (parts.length != 2) {
                throw new CustomException(ErrorCode.BASIC_AUTH_FAILED);
            }

            String username = parts[0];
            String password = parts[1];

            if (!VALID_USERNAME.equals(username) || !VALID_PASSWORD.equals(password)) {
                throw new CustomException(ErrorCode.BASIC_AUTH_FAILED);
            }
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.BASIC_AUTH_FAILED);
        }

        filterChain.doFilter(request, response);
    }
}
