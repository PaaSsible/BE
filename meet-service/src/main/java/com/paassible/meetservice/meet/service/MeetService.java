package com.paassible.meetservice.meet.service;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import com.paassible.meetservice.meet.dto.MeetCreateRequest;
import com.paassible.meetservice.meet.dto.MeetCreateResponse;
import com.paassible.meetservice.meet.dto.MeetJoinResponse;
import com.paassible.meetservice.meet.entity.Meet;
import com.paassible.meetservice.meet.entity.MeetingStatus;
import com.paassible.meetservice.meet.entity.Participant;
import com.paassible.meetservice.meet.entity.ParticipantStatus;
import com.paassible.meetservice.meet.event.ParticipantJoinedEvent;
import com.paassible.meetservice.meet.event.ParticipantLeftEvent;
import com.paassible.meetservice.meet.repository.MeetRepository;
import com.paassible.meetservice.meet.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final ApplicationEventPublisher eventPublisher;

    public MeetCreateResponse createMeet(Long userId, MeetCreateRequest request) {
        meetValidator.validateUserInBoard(request.boardId(), userId);

        Meet meet = Meet.create(
                request.boardId(),
                userId,
                request.startTime() != null ? request.startTime() : LocalDateTime.now()
        );

        try {
            Meet savedMeet = meetRepository.save(meet);

            Participant host = Participant.create(savedMeet.getId(), userId);
            participantRepository.save(host);
            savedMeet.incrementParticipantCount();

            eventPublisher.publishEvent(new ParticipantJoinedEvent(savedMeet.getId(), userId));
            return MeetCreateResponse.from(savedMeet, host);

        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.MEET_ALREADY_EXISTS);
        }
    }

    public MeetJoinResponse joinMeet(Long meetId, Long userId) {

        Meet meet = meetValidator.validateMeetOngoing(meetId);
        meetValidator.validateUserInBoard(meet.getBoardId(), userId);

        try {
            Participant participant = participantRepository
                    .findByMeetIdAndUserIdWithLock(meetId, userId)
                    .map(p -> {
                        if (p.getStatus() == ParticipantStatus.JOINED) {
                            throw new CustomException(ErrorCode.MEET_ALREADY_JOINED);
                        }
                        p.rejoin();
                        meet.incrementParticipantCount();
                        return p;
                    })
                    .orElseGet(() -> {
                        meet.incrementParticipantCount();
                        return participantRepository.save(Participant.create(meetId, userId));
                    });

            eventPublisher.publishEvent(new ParticipantJoinedEvent(meetId, userId));
            return MeetJoinResponse.from(participant);

        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.MEET_ALREADY_JOINED);
        }
    }

    public void leaveMeet(Long meetId, Long userId) {

        Meet meet = meetRepository.findByIdWithLock(meetId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEET_NOT_FOUND));

        if (meet.getStatus() == MeetingStatus.ENDED) {
            throw new CustomException(ErrorCode.MEET_ALREADY_ENDED);
        }

        Participant participant = meetValidator.getParticipantOrThrow(meetId, userId);
        meetValidator.ensureParticipantJoined(participant);

        participant.leave();
        meet.decrementParticipantCount();

        if(meet.isEmpty()) {
            meet.end();
        }

        eventPublisher.publishEvent(new ParticipantLeftEvent(meetId, userId));
    }
}
