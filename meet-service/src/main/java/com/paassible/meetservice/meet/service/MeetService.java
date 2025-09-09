package com.paassible.meetservice.meet.service;

import com.paassible.common.response.ErrorCode;
import com.paassible.meetservice.exception.MeetException;
import com.paassible.meetservice.meet.dto.MeetCreateRequest;
import com.paassible.meetservice.meet.dto.MeetCreateResponse;
import com.paassible.meetservice.meet.dto.MeetJoinResponse;
import com.paassible.meetservice.meet.entity.Meet;
import com.paassible.meetservice.meet.entity.Participant;
import com.paassible.meetservice.meet.repository.MeetRepository;
import com.paassible.meetservice.meet.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
@Transactional
public class MeetService {

    private final RedisTemplate<String, String> redisTemplate;
    private final MeetRepository meetRepository;
    private final ParticipantRepository participantRepository;
    private final MeetValidator meetValidator;

    private String participantsKey(Long meetId) {
        return "meeting:" + meetId + ":participants";
    }

    public MeetCreateResponse createMeet(Long userId, MeetCreateRequest request) {
        meetValidator.validateUserInBoard(request.boardId(), userId);

        Meet meet = Meet.create(
                request.boardId(),
                userId,
                request.startTime() != null ? request.startTime() : LocalDateTime.now()
        );
        Meet savedMeet = meetRepository.save(meet);

        Participant host = Participant.create(savedMeet.getId(), userId);
        Participant savedParticipant = participantRepository.save(host);

        redisTemplate.opsForSet().add(participantsKey(savedMeet.getId()), userId.toString());
        return MeetCreateResponse.from(savedMeet, savedParticipant);
    }

    public MeetJoinResponse joinMeet(Long meetId, Long userId) {

        Meet meet = meetValidator.validateMeetOngoing(meetId);
        meetValidator.validateUserInBoard(meet.getBoardId(), userId);
        meetValidator.ensureNotAlreadyJoined(meetId, userId);

        Participant participant = participantRepository
                .findByMeetIdAndUserId(meetId, userId)
                .map(p -> { p.rejoin(); return p; })
                .orElseGet(() -> participantRepository.save(Participant.create(meetId, userId)));

        redisTemplate.opsForSet().add(participantsKey(meetId), userId.toString());

        return MeetJoinResponse.from(participant);
    }

    public void leaveMeet(Long meetId, Long userId) {
        meetValidator.validateMeetOngoing(meetId);
        Participant participant = meetValidator.getParticipantOrThrow(meetId, userId);

        meetValidator.ensureParticipantJoined(participant);

        participant.leave();
        redisTemplate.opsForSet().remove(participantsKey(meetId), userId.toString());

        Long remaining = redisTemplate.opsForSet().size(participantsKey(meetId));
        if (remaining != null && remaining == 0L) {
            Meet meet = meetRepository.findById(meetId)
                    .orElseThrow(() -> new MeetException(ErrorCode.MEET_NOT_FOUND));
            meet.end();
            redisTemplate.delete(participantsKey(meetId));
        }
    }
}
