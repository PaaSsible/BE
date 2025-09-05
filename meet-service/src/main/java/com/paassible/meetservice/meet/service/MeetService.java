package com.paassible.meetservice.meet.service;

import com.paassible.meetservice.client.board.BoardClient;
import com.paassible.meetservice.meet.dto.MeetCreateRequest;
import com.paassible.meetservice.meet.dto.MeetCreateResponse;
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
    private final BoardClient boardClient;
    private final ParticipantRepository participantRepository;

    public MeetCreateResponse createMeet(Long hostId, MeetCreateRequest request) {

       boardClient.validateBoard(request.boardId());
       boardClient.validateUserInBoard(request.boardId(), hostId);

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
}
