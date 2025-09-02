package com.paassible.chatservice.chat.service;

import com.paassible.chatservice.chat.dto.ChatMessageRequest;
import com.paassible.chatservice.chat.dto.ChatMessageResponse;
import com.paassible.chatservice.chat.entity.ChatMessage;
import com.paassible.chatservice.chat.entity.ChatRoom;
import com.paassible.chatservice.chat.entity.MessageType;
import com.paassible.chatservice.chat.repository.ChatMessageRepository;
import com.paassible.chatservice.chat.repository.ChatRoomRepository;
import com.paassible.chatservice.client.UserClient;
import com.paassible.chatservice.client.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserClient userClient;

    @Transactional
    public ChatMessageResponse saveMessage(Long roomId, ChatMessageRequest request) {
        ChatRoom room = chatRoomRepository.findById(roomId).orElseThrow();
        UserResponse user = userClient.getUser(request.getSenderId());

        ChatMessage msg = ChatMessage.builder()
                .roomId(room.getId())
                .senderId(user.getId())
                .content(request.getContent())
                .type(request.getType())
                .build();

        chatMessageRepository.save(msg);

        return ChatMessageResponse.from(msg, user);
    }
}
