package com.paassible.meetservice.config;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import com.paassible.common.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/meet")
                .setAllowedOriginPatterns(
                        "http://localhost:*",
                        "http://127.0.0.1:*",
                        "https://paassible.kro.kr",
                        "https://meet.paassible.kro.kr"
                );
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);


                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authHeader = accessor.getFirstNativeHeader("Authorization");

                    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        log.warn("Authorization 헤더 없음 또는 형식 오류");
                        throw new CustomException(ErrorCode.APPLICATION_UNAUTHORIZED);
                    }


                    String token = authHeader.substring(7);
                    if (!jwtUtil.validateToken(token)) {
                        log.error("유효하지 않은 JWT 토큰");
                        throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
                    }

                    Long userId = jwtUtil.getUserId(token);

                    // Principal을 Authentication으로 명시 세팅
                    accessor.setUser(new UsernamePasswordAuthenticationToken(
                            userId.toString(), null, List.of()
                    ));

                    log.debug("STOMP CONNECT 인증 성공 userId={}", userId);
                }
                return message;
            }
        });
    }
}
