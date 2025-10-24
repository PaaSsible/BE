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

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final RoomParticipantService roomParticipantService;
    private final ChatMessageMapper chatMessageMapper;

    private final UserClient userClient;
    private final ObjectStorageService fileStorageService;

    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public ChatMessageResponse saveMessage(Long roomId, Long userId, ChatMessageRequest request) {
        chatRoomService.validateRoom(roomId);
        roomParticipantService.validateRoomParticipant(roomId, userId);
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

    public CursorPageResponse<ChatMessageResponse> getMessages(Long userId, Long roomId, Long cursor, int size, String direction) {
        chatRoomService.validateRoom(roomId);
        roomParticipantService.validateRoomParticipant(roomId, userId);

        List<ChatMessage> messages;
        Pageable pageable = PageRequest.of(0, size+1);

        if (cursor == null) {
            messages = chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable);
        } else {
            ChatMessage cursorMessage = chatMessageRepository.findById(cursor)
                    .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CURSOR));

            if ("down".equals(direction)) {
                messages = chatMessageRepository.findMessagesAfter(roomId, cursorMessage.getCreatedAt(), pageable);
            } else {
                messages = chatMessageRepository.findMessagesBefore(roomId, cursorMessage.getCreatedAt(), pageable);
            }
        }

        boolean hasNext = messages.size() > size;
        if (hasNext) {
            messages = messages.subList(0, size);
        }

        messages.sort(Comparator.comparing(ChatMessage::getCreatedAt));

        Long nextCursor = null;
        if (!messages.isEmpty()) {
            if ("down".equals(direction)) {
                nextCursor = messages.get(messages.size() - 1).getId();
            } else {
                nextCursor = messages.get(0).getId();
            }
        }

        List<ChatMessageResponse> responseItems = chatMessageMapper.toResponseList(messages);
        return new CursorPageResponse<>(responseItems, nextCursor, hasNext);
    }

    public MessageSearchResponse searchMessages(Long userId, Long roomId, String keyword) {
        chatRoomService.validateRoom(roomId);
        roomParticipantService.validateRoomParticipant(roomId, userId);

        List<Long> ids = chatMessageRepository.searchIdsByKeyword(roomId, keyword);
        return new MessageSearchResponse(ids.size(), ids);
    }

    public MessageAroundResponse getAroundMessages(Long userId, Long roomId, Long messageId, int limit) {
        chatRoomService.validateRoom(roomId);
        validateMessageInRoom(messageId, roomId);
        roomParticipantService.validateRoomParticipant(roomId, userId);

        ChatMessage center = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_MESSAGE_NOT_FOUND));

        List<ChatMessage> before = chatMessageRepository.findMessagesBefore(roomId, center.getCreatedAt(), PageRequest.of(0, limit));
        before.sort(Comparator.comparing(ChatMessage::getCreatedAt));

        List<ChatMessage> after = chatMessageRepository.findMessagesAfter(roomId, center.getCreatedAt(), PageRequest.of(0, limit));

        return new MessageAroundResponse(
                chatMessageMapper.toResponseList(before),
                chatMessageMapper.toResponse(center),
                chatMessageMapper.toResponseList(after)
        );
    }

    @Transactional
    public void markAsRead(Long userId, Long roomId, Long lastMessageId) {
        chatRoomService.validateRoom(roomId);
        validateMessageInRoom(lastMessageId, roomId);
        roomParticipantService.validateRoomParticipant(roomId, userId);

        RoomParticipant participant = roomParticipantService.getRoomParticipant(roomId, userId);

        Long oldLastReadMessageId = participant.getLastReadMessageId();
        if ( oldLastReadMessageId == null || oldLastReadMessageId < lastMessageId) {
            participant.updateLastReadMessageId(lastMessageId);

            ChatMessage message = getChatMessage(lastMessageId);
            if (!message.getSenderId().equals(userId)) {
                MessageReadResponse response = MessageReadResponse.from(userId, oldLastReadMessageId, lastMessageId);
                messagingTemplate.convertAndSend("/topic/chats/rooms/" + roomId + "/read", response);
            }
        }
    }

    @Transactional(readOnly = true)
    public MessageReadDetailResponse getMessageReadDetail(Long userId, Long roomId, Long messageId) {
        chatRoomService.validateRoom(roomId);
        validateMessageInRoom(messageId, roomId);
        roomParticipantService.validateRoomParticipant(roomId, userId);

        List<RoomParticipant> participants = roomParticipantService.getAllByRoomId(roomId);
        ChatMessage message = getChatMessage(messageId);

        List<MessageReadUserResponse> readUsers = participants.stream()
                .filter(p -> !p.getUserId().equals(message.getSenderId()))
                .filter(p -> p.getLastReadMessageId() != null && p.getLastReadMessageId() >= messageId)
                .map(p -> userClient.getUser(p.getUserId()).getNickname())
                .map(MessageReadUserResponse::from)
                .toList();

        return MessageReadDetailResponse.from(messageId, readUsers);
    }

    public ChatMessage getChatMessage(Long messageId) {
        return chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_MESSAGE_NOT_FOUND));
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
