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
                        // JWT 토큰 인증 (Authorization: Bearer <token>)
                        .addSecuritySchemes("access-token",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))

                        // BasicAuth 대신 커스텀 헤더 사용 (X-Basic-Auth)
                        .addSecuritySchemes("basicAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-Basic-Auth"))
                )
                .addSecurityItem(new SecurityRequirement()
                        .addList("access-token")
                        .addList("basicAuth"))
                .info(info());
    }

}
