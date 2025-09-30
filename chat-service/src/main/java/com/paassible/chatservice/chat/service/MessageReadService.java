package com.paassible.chatservice.chat.service;

import com.paassible.chatservice.chat.dto.MessageReadDetailResponse;
import com.paassible.chatservice.chat.dto.MessageReadResponse;
import com.paassible.chatservice.chat.entity.ChatMessage;
import com.paassible.chatservice.chat.entity.ChatRoom;
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

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    // 이거 서비스로 뺄지
    private final RoomParticipantRepository roomParticipantRepository;
    private final UserClient userClient;

    @Transactional
    public MessageReadResponse markAsRead(Long userId, Long roomId, Long messageId) {
        chatRoomService.validateRoom(roomId);
        chatMessageService.validateMessageInRoom(messageId, roomId);

        RoomParticipant participant = roomParticipantRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_PARTICIPANT_NOT_FOUND));

        if (participant.getLastReadMessageId() == null ||
                participant.getLastReadMessageId() < messageId) {
            participant.updateLastReadMessageId(messageId);
        }

        // 읽은 사람 수 조회
        long readCount = roomParticipantRepository.countReaders(roomId, messageId);

        // 안 읽은 사람 수 조회
        //long totalParticipants = roomParticipantRepository.countByRoomId(roomId);
        //long unreadCount = totalParticipants - readCount;

        return MessageReadResponse.from(messageId, readCount);
    }

    @Transactional(readOnly = true)
    public MessageReadDetailResponse getMessageReadDetail(Long userId, Long roomId, Long messageId) {
        chatRoomService.validateRoom(roomId);
        chatMessageService.validateMessageInRoom(messageId, roomId);

        if (!roomParticipantRepository.existsByRoomIdAndUserId(roomId, userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ROOM_ACCESS);
        }

        List<RoomParticipant> participants =
                roomParticipantRepository.findAllByRoomId(messageId);

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
        return MessageReadDetailResponse.from(messageId, readUsers, unreadUsers);
    }
}
