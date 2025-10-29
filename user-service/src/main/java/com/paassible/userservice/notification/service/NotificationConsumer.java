package com.paassible.userservice.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paassible.userservice.notification.config.RabbitConfig;
import com.paassible.userservice.notification.dto.NotificationMessageDto;
import com.paassible.userservice.notification.entity.Notification;
import com.paassible.userservice.notification.entity.enums.NoticeType;
import com.paassible.userservice.notification.repository.NotificationRepository;
import com.paassible.userservice.notification.sse.SseEmitterManager;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;
    private final SseEmitterManager emitterManager;

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void handleMessage(String messageJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            NotificationMessageDto msg = mapper.readValue(messageJson, NotificationMessageDto.class);

            Notification notification = Notification.builder()
                            .userId(msg.getUserId())
                            .type(NoticeType.valueOf(msg.getType()))
                            .title(msg.getTitle())
                            .content(msg.getContent())
                            .build();
            notificationRepository.save(notification);

            emitterManager.send(msg.getUserId(), notification);

        } catch (Exception e) {
            System.err.println("consume error: " + e.getMessage());
        }
    }
}
