package com.paassible.chatservice.chat.service;

import com.paassible.chatservice.chat.dto.MessageReadDetailResponse;
import com.paassible.chatservice.chat.dto.MessageReadResponse;
import com.paassible.chatservice.chat.entity.ChatMessage;
import com.paassible.chatservice.chat.entity.RoomParticipant;
import com.paassible.chatservice.chat.repository.ChatMessageRepository;
import com.paassible.chatservice.chat.repository.RoomParticipantRepository;
import com.paassible.chatservice.client.user.UserClient;
import com.paassible.chatservice.client.user.UserResponse;
import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageReadService {

    private final ChatMessageRepository chatMessageRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final UserClient userClient;

    @Transactional
    public MessageReadResponse markAsRead(Long userId, Long roomId, Long messageId) {
        RoomParticipant participant = roomParticipantRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_PARTICIPANT_NOT_FOUND));

        if (participant.getLastReadMessageId() == null ||
                participant.getLastReadMessageId() < messageId) {
            participant.updateLastReadMessageId(messageId);
        }

        roomParticipantRepository.save(participant);

        return MessageReadResponse.from(roomId, userId, messageId);
    }

    @Transactional(readOnly = true)
    public MessageReadDetailResponse getMessageReadDetail(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_MESSAGE_NOT_FOUND));

        List<RoomParticipant> participants =
                roomParticipantRepository.findAllByRoomId(message.getRoomId());

        List<UserResponse> readUsers = new ArrayList<>();
        List<UserResponse> unreadUsers = new ArrayList<>();

        for (RoomParticipant participant : participants) {
            UserResponse user = userClient.getUser(participant.getUserId());

            if (participant.getLastReadMessageId() != null
                    && participant.getLastReadMessageId() >= messageId) {
                readUsers.add(user);
            } else {
                unreadUsers.add(user);
            }
        }

        return MessageReadDetailResponse.builder()
                .messageId(messageId)
                .readUsers(readUsers)
                .unreadUsers(unreadUsers)
                .build();
    }
}
