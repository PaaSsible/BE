package com.paassible.common.security.config;

import com.paassible.common.security.interceptor.TermsCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final TermsCheckInterceptor termsCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(termsCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/users/auth/**", "/users/test/**", "/users/terms");
    }
}