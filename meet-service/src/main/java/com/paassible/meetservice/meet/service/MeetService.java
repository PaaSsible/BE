package com.paassible.meetservice.meet.service;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import com.paassible.meetservice.client.board.BoardClient;
import com.paassible.meetservice.client.board.BoardMemberResponse;
import com.paassible.meetservice.client.user.UserClient;
import com.paassible.meetservice.client.user.UserResponse;
import com.paassible.meetservice.meet.dto.*;
import com.paassible.meetservice.meet.entity.Meet;
import com.paassible.meetservice.meet.entity.MeetingStatus;
import com.paassible.meetservice.meet.entity.Participant;
import com.paassible.meetservice.meet.entity.ParticipantStatus;
import com.paassible.meetservice.meet.event.ParticipantJoinedEvent;
import com.paassible.meetservice.meet.event.ParticipantLeftEvent;
import com.paassible.meetservice.meet.repository.MeetRepository;
import com.paassible.meetservice.meet.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MeetService {

    private final MeetRepository meetRepository;
    private final ParticipantRepository participantRepository;
    private final MeetValidator meetValidator;
    private final ApplicationEventPublisher eventPublisher;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final BoardClient boardClient;
    private final RedisTemplate<String,String> redisTemplate;
    private final UserClient userClient;

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

            eventPublisher.publishEvent(new ParticipantJoinedEvent(savedMeet, userId));

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

            eventPublisher.publishEvent(new ParticipantJoinedEvent(meet, userId));

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

        eventPublisher.publishEvent(new ParticipantLeftEvent(meet, userId));
    }


    @Transactional(readOnly = true)
    public MeetOngoingResponse getOngoingMeetByBoard(Long boardId){
        boardClient.validateBoard(boardId);

        Meet meet = meetRepository.findByBoardIdAndStatus(boardId, MeetingStatus.ONGOING)
                .orElse(null);

        if(meet == null) {
            return null;
        }

        Set<String> redisMembers = redisTemplate.opsForSet().members("meeting:" + meet.getId() + ":participants");
        List<Long> joinedIds = redisMembers == null
                ?List.of()
                :redisMembers.stream().map(Long::valueOf).toList();

        List<BoardMemberResponse> allMembers = boardClient.getBoardMembers(meet.getBoardId());
        List<BoardMemberResponse> presentMembers = allMembers.stream()
                .filter(member -> joinedIds.contains(member.getUserId()))
                .toList();

        return MeetOngoingResponse.from(meet, presentMembers);
    }

    @Transactional
    public void transferHost(Long meetId, Long currentHostId, Long newHostId){
        Meet meet = meetRepository.findByIdWithLock(meetId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEET_NOT_FOUND));

        if(!meet.getHostId().equals(currentHostId)) {
            throw new CustomException(ErrorCode.MEET_NOT_HOST);
        }

        Participant newHost = participantRepository.findByMeetIdAndUserId(meetId, newHostId)
                .orElseThrow(()-> new CustomException(ErrorCode.MEET_NOT_PARTICIPANT));
        if(newHost.getStatus() != ParticipantStatus.JOINED) {
            throw new CustomException(ErrorCode.PARTICIPANT_ALREADY_LEFT);
        }

        meet.updateHost(newHostId);
        meetRepository.save(meet);

        UserResponse newHostInfo = userClient.getUser(newHostId);

        simpMessagingTemplate.convertAndSend(
                "/topic/meet/" + meetId + "/host",
                new HostChangedMessage(meetId, newHostId, newHostInfo.getNickname())
        );
    }

}
