package com.paassible.chatservice.chat.service;

import com.paassible.chatservice.chat.entity.RoomParticipant;
import com.paassible.chatservice.chat.repository.RoomParticipantRepository;
import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomParticipantService {

    private final RoomParticipantRepository roomParticipantRepository;

    public void saveRoomParticipant(Long roomId, Long userId) {
        RoomParticipant participant = RoomParticipant.builder()
                .roomId(roomId)
                .userId(userId)
                .build();
        roomParticipantRepository.save(participant);
    }

    public RoomParticipant getRoomParticipant(Long roomId, Long userId) {
        return roomParticipantRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_PARTICIPANT_NOT_FOUND));
    }

    public List<RoomParticipant> getRoomParticipantsByBoardId(Long userId, Long boardId) {
        return roomParticipantRepository.findByUserIdAndBoardId(userId, boardId);
    }

    public List<RoomParticipant> getAllByRoomId(Long roomId) {
        return roomParticipantRepository.findAllByRoomId(roomId);
    }

    public boolean existsByRoomId(Long roomId) {
        return roomParticipantRepository.existsByRoomId(roomId);
    }

    public void validateRoomParticipant(Long roomId, Long userId) {
        if (!roomParticipantRepository.existsByRoomIdAndUserId(roomId, userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ROOM_ACCESS);
        }
    }

    public Long countReaders(Long roomId, Long userId, Long messageId) {
        return roomParticipantRepository.countReaders(roomId, userId, messageId);
    }

    public void deleteRoomParticipant(Long roomId, Long userId) {
        roomParticipantRepository.deleteByRoomIdAndUserId(roomId, userId);
    }
}
