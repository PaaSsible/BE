package com.paassible.chatservice.chat.service;

import com.paassible.chatservice.chat.dto.*;
import com.paassible.chatservice.chat.entity.ChatMessage;
import com.paassible.chatservice.chat.entity.ChatRoom;
import com.paassible.chatservice.chat.entity.RoomParticipant;
import com.paassible.chatservice.chat.entity.enums.MessageType;
import com.paassible.chatservice.chat.entity.enums.RoomType;
import com.paassible.chatservice.chat.exception.ChatException;
import com.paassible.chatservice.chat.repository.ChatMessageRepository;
import com.paassible.chatservice.chat.repository.ChatRoomRepository;
import com.paassible.chatservice.client.board.BoardClient;
import com.paassible.chatservice.client.user.UserClient;
import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final RoomParticipantService roomParticipantService;
    private final UserClient userClient;

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatPublisher publisher;

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getChatRooms(Long userId, Long boardId) {
        List<RoomParticipant> roomParticipants = roomParticipantService.getRoomParticipantsByBoardId(userId, boardId);

        List<ChatRoomResponse> responses = new ArrayList<>();

        for (RoomParticipant roomParticipant : roomParticipants) {
            Long roomId = roomParticipant.getRoomId();

            ChatRoom room = chatRoomRepository.findById(roomId)
                    .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

            ChatMessage lastMessage = chatMessageRepository.findTopByRoomIdAndTypeNotOrderByCreatedAtDesc(roomId, MessageType.SYSTEM);
            String lastMessageContent = (lastMessage != null) ? lastMessage.getContent() : null;
            LocalDateTime lastMessageTime = (lastMessage != null) ? lastMessage.getCreatedAt() : null;

            int unreadCount;
            if (roomParticipant.getLastReadMessageId() == null) {
                unreadCount = chatMessageRepository.countByRoomId(roomId);
            } else {
                unreadCount = chatMessageRepository.countUnreadMessages(roomId, roomParticipant.getLastReadMessageId());
            }
            responses.add(ChatRoomResponse.from(room.getId(), room.getName(), lastMessageContent, lastMessageTime, unreadCount));
        }
        return responses;
    }

    @Transactional
    public ChatRoomIdResponse createChatRoom(Long userId, Long boardId, ChatRoomRequest request) {
        List<Long> participantIds = request.getParticipantIds();
        if (!participantIds.contains(userId)) {
            participantIds.add(userId);
        }

        String roomName;
        if (participantIds.size() == 2) {
            Long otherId = participantIds.stream()
                    .filter(id -> !id.equals(userId))
                    .findFirst()
                    .orElseThrow();
            roomName = userClient.getUser(otherId).getNickname();
        } else {
            roomName = participantIds.stream()
                    .filter(id -> !id.equals(userId))
                    .map(id -> userClient.getUser(id).getNickname())
                    .collect(Collectors.joining(", "));
        }

        ChatRoom newRoom = ChatRoom.builder()
                .type(RoomType.SUB)
                .boardId(boardId)
                .name(roomName)
                .build();
        ChatRoom savedRoom = chatRoomRepository.save(newRoom);

        for (Long id : participantIds) {
            roomParticipantService.saveRoomParticipant(savedRoom.getId(), id);
        }

        publisher.publishChatRoomCreated(participantIds, roomName);

        return new ChatRoomIdResponse(savedRoom.getId());
    }

    @Transactional
    public void createGroupChat(Long userId, Long boardId, String boardName) {
        ChatRoom room = ChatRoom.builder()
                .type(RoomType.GROUP)
                .name(boardName)
                .boardId(boardId)
                .build();
        chatRoomRepository.save(room);

        addGroupParticipant(userId, boardId);
    }

    @Transactional
    public void addGroupParticipant(Long userId, Long boardId) {
        ChatRoom room = chatRoomRepository.findByBoardIdAndType(boardId, RoomType.GROUP)
                .orElseThrow(() -> new ChatException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        roomParticipantService.saveRoomParticipant(room.getId(), userId);
    }

    @Transactional
    public void addSubParticipant(Long userId, Long boardId, Long roomId, ChatRoomInviteRequest request) {
        ChatRoom room = chatRoomRepository.findByIdAndBoardId(roomId, boardId)
                .orElseThrow(() -> new ChatException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (!room.getBoardId().equals(boardId) || room.getType() != RoomType.SUB) {
            throw new CustomException(ErrorCode.INVALID_ROOM_TYPE);
        }
        roomParticipantService.validateRoomParticipant(roomId, userId);

        List<Long> participantIds = request.getParticipantIds();
        for (Long id : participantIds) {
            roomParticipantService.saveRoomParticipant(roomId, id);
        }

        String names = participantIds.stream()
                .map(id -> userClient.getUser(id).getNickname())
                .collect(Collectors.joining(", "));

        String updatedName = room.getName() + ", " + names;
        room.updateRoomName(updatedName);

        ChatMessage systemMsg = ChatMessage.builder()
                .roomId(roomId)
                .type(MessageType.SYSTEM)
                .content(names + "님이 들어왔습니다.")
                .build();
        chatMessageRepository.save(systemMsg);

        messagingTemplate.convertAndSend("/topic/chats/rooms/" + roomId + "/system",
                SystemMessageResponse.from(systemMsg));

    }

    @Transactional
    public void leaveRoom(Long userId, Long boardId, Long roomId) {
        validateRoom(roomId);
        roomParticipantService.validateRoomParticipant(roomId, userId);

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        if (!room.getBoardId().equals(boardId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ROOM_ACCESS);
        }

        String nickname = userClient.getUser(userId).getNickname();
        String updatedName = Arrays.stream(room.getName().split(","))
                .map(String::trim)
                .filter(name -> !name.equals(nickname))
                .collect(Collectors.joining(", "));
        room.updateRoomName(updatedName);

        roomParticipantService.deleteRoomParticipant(roomId, userId);

        ChatMessage systemMsg = ChatMessage.builder()
                .roomId(roomId)
                .type(MessageType.SYSTEM)
                .content(nickname + "님이 채팅방을 나갔습니다.")
                .build();
        chatMessageRepository.save(systemMsg);

        messagingTemplate.convertAndSend("/topic/chats/rooms/" + roomId + "/system",
                SystemMessageResponse.from(systemMsg));

        if (!roomParticipantService.existsByRoomId(roomId)) {
            chatRoomRepository.deleteById(roomId);
        }
    }


    public void validateRoom(Long roomId) {
        if (!chatRoomRepository.existsById(roomId)) {
            throw new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }
    }
}

