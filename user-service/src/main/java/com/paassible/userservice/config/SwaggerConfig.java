package com.paassible.userservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    private Info info() {
        return new Info()
                .title("PaaSsible팀 비대면 협업 서비스 API 문서")
                .version("1.0")
                .description("User Service");
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        // JWT 토큰 인증
                        .addSecuritySchemes("access-token",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))
                        // Basic Auth 인증
                        .addSecuritySchemes("basicAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic"))
                )
                .addSecurityItem(new SecurityRequirement().addList("access-token"))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
                .info(info());
    }
}
