package com.paassible.chatservice.chat.service;

import com.paassible.chatservice.chat.dto.ChatRoomResponse;
import com.paassible.chatservice.chat.dto.JoinDirectRequest;
import com.paassible.chatservice.chat.entity.ChatRoom;
import com.paassible.chatservice.chat.entity.RoomParticipant;
import com.paassible.chatservice.chat.entity.RoomType;
import com.paassible.chatservice.chat.exception.ChatException;
import com.paassible.chatservice.chat.repository.ChatRoomRepository;
import com.paassible.chatservice.chat.repository.RoomParticipantRepository;
import com.paassible.chatservice.client.UserClient;
import com.paassible.chatservice.client.UserResponse;
import com.paassible.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final RoomParticipantRepository participantRepository;
    private final UserClient userClient;

    /**
     * 보드 채팅방 조회
     * - 보드 생성 시 room이 이미 만들어져 있다고 가정
     * - 단순히 boardId로 연결된 room을 찾아 반환
     * - 추가 방을 만든다면 여기에 boardId로 여러 보드를 찾아와서 보여주기 + 보드 이름만들기
     */
    @Transactional(readOnly = true)
    public ChatRoomResponse getGroupChatRoom(Long boardId) {
        ChatRoom room = chatRoomRepository.findByBoardIdAndType(boardId, RoomType.GROUP)
                .orElseThrow(() -> new ChatException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        return ChatRoomResponse.from(room);
    }

    /**
     * 1:1 채팅방 조회/생성
     * - 두 유저가 속한 DIRECT 방이 있으면 재사용
     * - 없으면 새로 생성하고 RoomParticipant 등록
     */
    @Transactional
    public ChatRoomResponse getOrCreateDirectRoom(JoinDirectRequest request) {

        Long userAId = request.getUserAId();
        Long userBId = request.getUserBId();

        ChatRoom room = chatRoomRepository.findDirectRoom(userAId, userBId)
                .orElseGet(() -> {
                    ChatRoom newRoom = ChatRoom.builder()
                            .type(RoomType.DIRECT)
                            .build();
                    ChatRoom savedRoom = chatRoomRepository.save(newRoom);

                    for (Long userId : List.of(userAId, userBId)) {
                        UserResponse user = userClient.getUser(userId);
                        RoomParticipant participant = RoomParticipant.builder()
                                .roomId(savedRoom.getId())
                                .userId(user.getId())
                                .build();
                        participantRepository.save(participant);
                    }
                    return savedRoom;
                });

        return ChatRoomResponse.from(room);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getRoomsByUser(Long userId) {
        List<RoomParticipant> participants = participantRepository.findByUserId(userId);

        return participants.stream()
                .map(p -> chatRoomRepository.findById(p.getRoomId())
                        .orElseThrow(() -> new ChatException(ErrorCode.CHAT_ROOM_NOT_FOUND)))
                .map(ChatRoomResponse::from)
                .toList();
    }

    @Transactional
    public void createBoardChatRoom(Long userId, Long boardId) {
        ChatRoom room = ChatRoom.builder()
                .type(RoomType.GROUP)
                .boardId(boardId)
                .build();
        chatRoomRepository.save(room);

        addParticipant(userId, boardId);
    }

    @Transactional
    public void addParticipant(Long userId, Long boardId) {
        ChatRoom room = chatRoomRepository.findByBoardId(boardId)
                .orElseThrow(() -> new ChatException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        UserResponse user = userClient.getUser(userId);

        RoomParticipant participant = RoomParticipant.builder()
                .roomId(room.getId())
                .userId(user.getId())
                .build();
        participantRepository.save(participant);
    }
}

