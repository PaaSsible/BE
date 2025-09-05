package com.paassible.meetservice.meet.service;

import com.paassible.common.response.ErrorCode;
import com.paassible.meetservice.client.board.BoardClient;
import com.paassible.meetservice.exception.MeetException;
import com.paassible.meetservice.meet.entity.Meet;
import com.paassible.meetservice.meet.repository.MeetRepository;
import com.paassible.meetservice.meet.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetValidator {
    private final BoardClient boardClient;
    private final MeetRepository meetRepository;
    private final ParticipantRepository participantRepository;

    public void validateUserInBoard(Long boardId, Long userId) {
        boardClient.validateBoard(boardId);
        boardClient.validateUserInBoard(boardId, userId);

    }

    public Long validateMeet(Long meetId) {
       Meet meet = meetRepository.findById(meetId)
               .orElseThrow(()-> new MeetException(ErrorCode.MEET_NOT_FOUND));
       return meet.getBoardId();
    }

    public void validateUserInMeet (Long meetId, Long userId) {
        if(participantRepository.existsByMeetIdAndUserId(meetId, userId)){
            throw new MeetException(ErrorCode.MEET_ALREADY_JOINED);
        }
    }
}
