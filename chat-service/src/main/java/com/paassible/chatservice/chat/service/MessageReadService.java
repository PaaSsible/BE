package com.paassible.chatservice.chat.service;

import com.paassible.chatservice.chat.dto.MessageReadAllResponse;
import com.paassible.chatservice.chat.dto.MessageReadDetailResponse;
import com.paassible.chatservice.chat.dto.MessageReadResponse;
import com.paassible.chatservice.chat.dto.MessageReadUserResponse;
import com.paassible.chatservice.chat.entity.ChatMessage;
import com.paassible.chatservice.chat.entity.RoomParticipant;
import com.paassible.chatservice.chat.entity.enums.ReadType;
import com.paassible.chatservice.client.user.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageReadService {

    private final ChatRoomService chatRoomService;
    private final RoomParticipantService roomParticipantService;
    private final ChatRoomMessageService messageService;

    private final UserClient userClient;

    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void markAsRead(Long userId, Long roomId, Long lastMessageId, ReadType type) {
        chatRoomService.validateRoom(roomId);
        messageService.validateMessageInRoom(lastMessageId, roomId);
        roomParticipantService.validateRoomParticipant(roomId, userId);

        RoomParticipant participant = roomParticipantService.getRoomParticipant(roomId, userId);

        Long oldLastReadMessageId = participant.getLastReadMessageId();
        if ( oldLastReadMessageId == null || oldLastReadMessageId < lastMessageId) {
            participant.updateLastReadMessageId(lastMessageId);

            ChatMessage message = messageService.getChatMessage(lastMessageId);
            if (!message.getSenderId().equals(userId)) {
                Object response;
                if (type == ReadType.MESSAGE_READ) {
                    response = MessageReadResponse.from(userId, lastMessageId);
                } else {
                    response = MessageReadAllResponse.from(userId, oldLastReadMessageId, lastMessageId);
                }
                messagingTemplate.convertAndSend("/topic/chats/rooms/" + roomId + "/read", response);
            }
        }
    }

    @Transactional(readOnly = true)
    public MessageReadDetailResponse getMessageReadDetail(Long userId, Long roomId, Long messageId) {
        chatRoomService.validateRoom(roomId);
        messageService.validateMessageInRoom(messageId, roomId);
        roomParticipantService.validateRoomParticipant(roomId, userId);

        List<RoomParticipant> participants = roomParticipantService.getAllByRoomId(roomId);
        ChatMessage message = messageService.getChatMessage(messageId);

        List<MessageReadUserResponse> readUsers = participants.stream()
                .filter(p -> !p.getUserId().equals(message.getSenderId()))
                .filter(p -> p.getLastReadMessageId() != null && p.getLastReadMessageId() >= messageId)
                .map(p -> userClient.getUser(p.getUserId()).getNickname())
                .map(MessageReadUserResponse::from)
                .toList();

        return MessageReadDetailResponse.from(messageId, readUsers);
    }
}
