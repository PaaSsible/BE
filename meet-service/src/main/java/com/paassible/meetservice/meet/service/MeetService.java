package com.paassible.meetservice.meet.service;

import com.paassible.meetservice.meet.dto.MeetCreateRequest;
import com.paassible.meetservice.meet.dto.MeetCreateResponse;
import com.paassible.meetservice.meet.dto.MeetJoinResponse;
import com.paassible.meetservice.meet.entity.Meet;
import com.paassible.meetservice.meet.entity.Participant;
import com.paassible.meetservice.meet.repository.MeetRepository;
import com.paassible.meetservice.meet.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetService {

    private final MeetRepository meetRepository;
    private final ParticipantRepository participantRepository;
    private final MeetValidator meetValidator;

    public MeetCreateResponse createMeet(Long hostId, MeetCreateRequest request) {

       meetValidator.validateUserInBoard(request.boardId(), hostId);
        Meet meet = Meet.create(
                request.boardId(),
                hostId,
                request.startTime() != null ? request.startTime() : LocalDateTime.now()
        );

        Meet savedMeet = meetRepository.save(meet);
        Participant participant = Participant.create(savedMeet.getId(), hostId);
        Participant savedParticipant = participantRepository.save(participant);

        return MeetCreateResponse.from(savedMeet,savedParticipant);
    }

    public MeetJoinResponse joinMeet(Long meetId, Long userId){
        Long boardId = meetValidator.validateMeet(meetId);
        meetValidator.validateUserInBoard(boardId, userId);
        meetValidator.validateUserInMeet(meetId, userId);

        Participant participant = Participant.create(meetId, userId);
        Participant savedParticipant = participantRepository.save(participant);

        return MeetJoinResponse.from(savedParticipant);
    }
}
