package com.paassible.chatservice.chat.service;

import com.paassible.chatservice.chat.dto.*;
import com.paassible.chatservice.chat.entity.ChatMessage;
import com.paassible.chatservice.chat.entity.enums.MessageType;
import com.paassible.chatservice.chat.repository.ChatMessageRepository;
import com.paassible.chatservice.client.user.UserClient;
import com.paassible.chatservice.client.user.UserResponse;
import com.paassible.chatservice.file.service.ObjectStorageService;
import com.paassible.chatservice.chat.dto.ChatPageResponse;
import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public ChatPageResponse getMessages(Long userId, Long roomId, int page, int size) {
        chatRoomService.validateRoom(roomId);
        roomParticipantService.validateRoomParticipant(roomId, userId);

        Pageable pageable = PageRequest.of(page, size);

        Page<ChatMessageResponse> messagePage =
                chatMessageRepository.findByRoomId(roomId, pageable)
                .map(chatMessageMapper::toResponse);

        return ChatPageResponse.from(messagePage);
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
