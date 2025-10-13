package com.paassible.common.security.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.ErrorCode;
import com.paassible.common.security.dto.UserJwtDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TermsCheckInterceptor implements HandlerInterceptor {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return true;
        }

        UserJwtDto dto = (UserJwtDto) auth.getPrincipal();

        if (!dto.isAgreedToTerms()) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json;charset=UTF-8");

            ApiResponse<Void> errorResponse =
                    ApiResponse.fail(ErrorCode.TERMS_NOT_AGREED);

            mapper.writeValue(response.getWriter(), errorResponse);

            return false;
        }
        return true;
    }
}
