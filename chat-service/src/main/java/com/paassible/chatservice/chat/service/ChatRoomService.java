package com.paassible.chatservice.chat.service;

import com.paassible.chatservice.chat.dto.ChatRoomResponse;
import com.paassible.chatservice.chat.dto.DirectChatRequest;
import com.paassible.chatservice.chat.dto.SubChatRequest;
import com.paassible.chatservice.chat.entity.ChatRoom;
import com.paassible.chatservice.chat.entity.RoomParticipant;
import com.paassible.chatservice.chat.entity.enums.RoomType;
import com.paassible.chatservice.chat.exception.ChatException;
import com.paassible.chatservice.chat.repository.ChatRoomRepository;
import com.paassible.chatservice.chat.repository.RoomParticipantRepository;
import com.paassible.chatservice.client.board.BoardClient;
import com.paassible.chatservice.client.user.UserClient;
import com.paassible.chatservice.client.user.UserResponse;
import com.paassible.common.exception.CustomException;
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
    private final BoardClient boardClient;

    /**
     * 보드 채팅방 조회
     * - 보드 생성 시 room이 이미 만들어져 있다고 가정
     * - 단순히 boardId로 연결된 room을 찾아 반환
     * - 추가 방을 만든다면 여기에 boardId로 여러 보드를 찾아와서 보여주기 + 보드 이름만들기
     */
    @Transactional(readOnly = true)
    public ChatRoomResponse getGroupChatRoom(Long userId, Long boardId) {
        boardClient.validateUserInBoard(userId, boardId);

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
    public ChatRoomResponse getOrCreateDirectChat(Long userId, DirectChatRequest request) {
        Long receiverId = request.getReceiverId();

        ChatRoom room = chatRoomRepository.findDirectRoom(userId, receiverId)
                .orElseGet(() -> {
                    ChatRoom newRoom = ChatRoom.builder()
                            .type(RoomType.DIRECT)
                            .build();
                    ChatRoom savedRoom = chatRoomRepository.save(newRoom);

                    for (Long id : List.of(userId, receiverId)) {
                        UserResponse user = userClient.getUser(id);
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

    @Transactional
    public void createGroupChat(Long userId, Long boardId) {
        boardClient.validateUserInBoard(userId, boardId);

        ChatRoom room = ChatRoom.builder()
                .type(RoomType.GROUP)
                // name으로 그냥 보드 이름 넣기?
                .boardId(boardId)
                .build();
        chatRoomRepository.save(room);

        addParticipant(userId, boardId);
    }

    public void createSubChat(Long userId, Long boardId, SubChatRequest request) {
        // 아니면 현재 프로젝트 보드의 모든 사람들을 가져와서 넣는 방식
        // board로 찾아와(해당 보드의 모든 사람들을)
        // 지금은 따로 멤버를 받고 있음
        boardClient.validateUserInBoard(userId, boardId);

        ChatRoom room = ChatRoom.builder()
                .type(RoomType.SUB)
                .boardId(boardId)
                .name(request.getName())
                .build();
        ChatRoom savedRoom = chatRoomRepository.save(room);

        List<Long> memberIds = request.getMemberIds();
        memberIds.forEach(memberId -> {
            RoomParticipant participant = RoomParticipant.builder()
                    .roomId(savedRoom.getId())
                    .userId(memberId)
                    .build();
            participantRepository.save(participant);
        });
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

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getRoomsByUser(Long userId) {
        List<RoomParticipant> participants = participantRepository.findByUserId(userId);

        return participants.stream()
                .map(p -> chatRoomRepository.findById(p.getRoomId())
                        .orElseThrow(() -> new ChatException(ErrorCode.CHAT_ROOM_NOT_FOUND)))
                .map(ChatRoomResponse::from)
                .toList();
    }

    public void validateRoom(Long roomId) {
        if (!chatRoomRepository.existsById(roomId)) {
            throw new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }
    }

    // 채팅방 목록 조회가 어떻게 될지..
    // 서브 채팅방이 멤버 구성이 어떻게 될지 일단 봐야함
    // 프로젝트 보드 목록을 조회할때 뭐 단체랑 서브를 각자 따로해야할지
}

