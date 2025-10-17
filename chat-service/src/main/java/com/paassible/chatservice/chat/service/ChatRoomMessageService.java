package com.paassible.chatservice.chat.service;

import com.paassible.chatservice.chat.dto.*;
import com.paassible.chatservice.chat.entity.ChatMessage;
import com.paassible.chatservice.chat.entity.RoomParticipant;
import com.paassible.chatservice.chat.entity.enums.MessageType;
import com.paassible.chatservice.chat.repository.ChatMessageRepository;
import com.paassible.chatservice.client.user.UserClient;
import com.paassible.chatservice.client.user.UserResponse;
import com.paassible.chatservice.file.service.ObjectStorageService;
import com.paassible.common.dto.CursorPageResponse;
import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final RoomParticipantService roomParticipantService;

    private final UserClient userClient;
    private final ObjectStorageService fileStorageService;

    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public ChatMessageResponse saveMessage(Long roomId, Long userId, ChatMessageRequest request) {
        chatRoomService.validateRoom(roomId);
        UserResponse user = userClient.getUser(userId);

        ChatMessage msg = ChatMessage.builder()
                .roomId(roomId)
                .senderId(user.getId())
                .content(request.getContent())
                .type(request.getType())
                .build();
        chatMessageRepository.save(msg);

        return ChatMessageResponse.from(msg, user, 0L);
    }

    public CursorPageResponse<ChatMessageResponse> getMessages(Long userId, Long roomId, Long cursor, int size) {
        chatRoomService.validateRoom(roomId);
        roomParticipantService.validateRoomParticipant(roomId, userId);

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
                    Long readCount = roomParticipantService.countReaders(m.getRoomId(), user.getId(), m.getId());
                    return ChatMessageResponse.from(m, user, readCount);
                })
                .toList();

        return new CursorPageResponse<>(responseItems, nextCursor, hasNext);
    }

    @Transactional
    public void markAsRead(Long userId, Long roomId, Long lastMessageId) {
        chatRoomService.validateRoom(roomId);
        validateMessageInRoom(lastMessageId, roomId);
        roomParticipantService.validateRoomParticipant(roomId, userId);

        RoomParticipant participant = roomParticipantService.getRoomParticipant(roomId, userId);
        if (participant.getLastReadMessageId() == null ||
                participant.getLastReadMessageId() < lastMessageId) {
            participant.updateLastReadMessageId(lastMessageId);
        }

        long readCount = roomParticipantService.countReaders(roomId, userId, lastMessageId);

        MessageReadResponse response = MessageReadResponse.from(lastMessageId, readCount);
        messagingTemplate.convertAndSend("/topic/chats/rooms/" + roomId + "/reads", response);
    }

    @Transactional(readOnly = true)
    public MessageReadDetailResponse getMessageReadDetail(Long userId, Long roomId, Long messageId) {
        chatRoomService.validateRoom(roomId);
        validateMessageInRoom(messageId, roomId);
        roomParticipantService.validateRoomParticipant(roomId, userId);

        List<RoomParticipant> participants = roomParticipantService.getAllByRoomId(roomId);
        List<MessageReadUserResponse> readUsers = participants.stream()
                .filter(p -> p.getLastReadMessageId() != null && p.getLastReadMessageId() >= messageId)
                .map(p -> userClient.getUser(p.getUserId()))
                .map(MessageReadUserResponse::from)
                .toList();

        return MessageReadDetailResponse.from(messageId, readUsers);
    }

    public void validateMessageInRoom(Long messageId, Long roomId) {
        if (!chatMessageRepository.existsByIdAndRoomId(messageId, roomId)) {
            throw new CustomException(ErrorCode.CHAT_MESSAGE_NOT_FOUND);
        }
    }

    public UploadResponse saveFile(MessageType type, MultipartFile file) {
        String url = fileStorageService.upload("chat", file);
        return UploadResponse .from(type, url);
    }
}
