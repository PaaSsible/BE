package com.paassible.recruitservice.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    private static final String EXCHANGE = "notifications";

    public void sendNotification(Long userId, String type, String title, String content) {
        try {
            NotificationMessageDto message = NotificationMessageDto.builder()
                    .userId(userId)
                    .type(type)
                    .title(title)
                    .content(content)
                    .build();

            String json = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(EXCHANGE, "recruit.event", json);
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }
}

