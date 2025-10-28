package com.paassible.chatservice.chat.service;

import com.paassible.chatservice.chat.dto.CommunicationResponse;
import com.paassible.chatservice.chat.entity.ChatMessage;
import com.paassible.chatservice.chat.repository.ChatMessageRepository;
import com.paassible.chatservice.chat.repository.ChatRoomRepository;
import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommunicationService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    public CommunicationResponse getCommunicationFrequency(Long userId, Long boardId) {
        Long roomId = chatRoomRepository.findGroupRoomIdByBoardId(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        long total = chatMessageRepository.countByRoomId(roomId);
        long value = chatMessageRepository.countByRoomIdAndSenderId(roomId, userId);

        return new CommunicationResponse(total, value);
    }
}
