package com.paassible.chatservice.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paassible.chatservice.chat.dto.NotificationMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    private static final String EXCHANGE = "notifications";

    public void publishChatRoomCreated(List<Long> invitedUserIds, String roomName) {
        for (Long userId : invitedUserIds) {
            try {
                NotificationMessageDto msg = NotificationMessageDto.builder()
                        .userId(userId)
                        .type("CHAT")
                        .title("새 채팅방 초대")
                        .content(roomName + " 채팅방에 초대되었습니다.")
                        .build();

                String json = objectMapper.writeValueAsString(msg);
                rabbitTemplate.convertAndSend(EXCHANGE, "chat.room_created", json);
            } catch (Exception e) {
                System.err.println("Failed to publish chat notification: " + e.getMessage());
            }
        }
    }
}
