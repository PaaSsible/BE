package com.paassible.meetservice.meet.service;

import com.paassible.common.response.ErrorCode;
import com.paassible.meetservice.client.board.BoardClient;
import com.paassible.meetservice.exception.MeetException;
import com.paassible.meetservice.meet.entity.Meet;
import com.paassible.meetservice.meet.entity.MeetingStatus;
import com.paassible.meetservice.meet.entity.Participant;
import com.paassible.meetservice.meet.entity.ParticipantStatus;
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

    public Meet validateMeetOngoing(Long meetId) {
        Meet meet = meetRepository.findById(meetId)
                .orElseThrow(() -> new MeetException(ErrorCode.MEET_NOT_FOUND));
        if (meet.getStatus() == MeetingStatus.ENDED) {
            throw new MeetException(ErrorCode.MEET_ALREADY_ENDED);
        }
        return meet;
    }

    public Long validateMeet(Long meetId) {
       Meet meet = meetRepository.findById(meetId)
               .orElseThrow(()-> new MeetException(ErrorCode.MEET_NOT_FOUND));
       return meet.getBoardId();
    }
    public void ensureNotAlreadyJoined(Long meetId, Long userId) {
        participantRepository.findByMeetIdAndUserId(meetId, userId)
                .ifPresent(p -> {
                    if (p.getStatus() == ParticipantStatus.JOINED) {
                        throw new MeetException(ErrorCode.MEET_ALREADY_JOINED);
                    }
                });
    }

    public Participant getParticipantOrThrow(Long meetId, Long userId) {
        return participantRepository.findByMeetIdAndUserId(meetId, userId)
                .orElseThrow(() -> new MeetException(ErrorCode.MEET_NOT_PARTICIPANT));
    }

    public void ensureParticipantJoined(Participant p) {
        if (p.getStatus() != ParticipantStatus.JOINED) {
            throw new MeetException(ErrorCode.PARTICIPANT_ALREADY_LEFT);
        }
    }


    public void validateUserInMeet (Long meetId, Long userId) {
        if(participantRepository.existsByMeetIdAndUserId(meetId, userId)){
            throw new MeetException(ErrorCode.MEET_ALREADY_JOINED);
        }
    }

    public void validateNotAlreadyJoined(Long meetId, Long userId) {
        participantRepository.findByMeetIdAndUserId(meetId, userId)
                .ifPresent(participant -> {
                    if(participant.getStatus() == ParticipantStatus.JOINED){
                        throw new MeetException(ErrorCode.MEET_ALREADY_JOINED);
                    }
                });
    }
}
