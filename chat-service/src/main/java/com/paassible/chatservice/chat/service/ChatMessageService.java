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
import com.paassible.common.dto.CursorPageResponse;
import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public CursorPageResponse<ChatMessageResponse> getMessages(Long roomId, Long cursor, int size) {
        List<ChatMessage> messages;
        Pageable pageable = PageRequest.of(0, size+1);

        if (cursor == null) {
            messages = chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable);
        } else {
            ChatMessage cursorMessage = chatMessageRepository.findById(cursor)
                    .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CURSOR));
            messages = chatMessageRepository.findMessagesBefore(roomId, cursorMessage.getCreatedAt(), pageable);
        }

        boolean hasNext = messages.size() > size;
        if (hasNext) {
            messages = messages.subList(0, size);
        }

        Long nextCursor = hasNext ? messages.get(messages.size() - 1).getId() : null;

        List<ChatMessageResponse> responseItems = messages.stream()
                .map(m -> {
                    UserResponse user = userClient.getUser(m.getSenderId());
                    return ChatMessageResponse.from(m, user);
                })
                .toList();

        return new CursorPageResponse<>(responseItems, nextCursor, hasNext);
    }
}
