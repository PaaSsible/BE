package com.paassible.chatservice.chat.service;

import com.paassible.chatservice.chat.dto.ChatMessageResponse;
import com.paassible.chatservice.chat.entity.ChatMessage;
import com.paassible.chatservice.client.user.UserClient;
import com.paassible.chatservice.client.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatMessageMapper {
    private final UserClient userClient;
    private final RoomParticipantService roomParticipantService;

    public ChatMessageResponse toResponse(ChatMessage m) {
        UserResponse user = userClient.getUser(m.getSenderId());
        Long readCount = roomParticipantService.countReaders(m.getRoomId(), user.getId(), m.getId());
        return ChatMessageResponse.from(m, user, readCount);
    }

    public List<ChatMessageResponse> toResponseList(List<ChatMessage> messages) {
        return messages.stream().map(this::toResponse).toList();
    }
}