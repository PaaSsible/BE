package com.paassible.meetservice.meet.service;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import com.paassible.meetservice.chat.dto.ChatMessage;
import com.paassible.meetservice.client.board.BoardClient;
import com.paassible.meetservice.client.board.BoardMemberResponse;
import com.paassible.meetservice.client.user.UserClient;
import com.paassible.meetservice.client.user.UserResponse;
import com.paassible.meetservice.meet.message.HostChangedMessage;
import com.paassible.meetservice.util.ChatKeys;
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
    private final RedisTemplate<String,String> chatRedisTemplate;
    private final RedisTemplate<String, String> stringRedis;
    private final RedisTemplate<String, ChatMessage> chatRedis;
    private final MeetCacheEventHandler meetCacheEventHandler;
    private final UserClient userClient;

    public MeetCreateResponse createMeet(Long userId, MeetCreateRequest request) {

        meetValidator.validateUserInBoard(request.boardId(), userId);
        meetValidator.ensureNoActiveMeetInBoard(request.boardId());

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

            eventPublisher.publishEvent(new ParticipantJoinedEvent(
                    savedMeet.getId(),
                    savedMeet.getBoardId(),
                    userId));

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

            eventPublisher.publishEvent(new ParticipantJoinedEvent(
                    meet.getId(),
                    meet.getBoardId(),
                    userId));

            return MeetJoinResponse.from(participant);

        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.MEET_ALREADY_JOINED);
        }
    }

    public LeaveResponse leaveMeet(Long meetId, Long userId) {

        Meet meet = meetRepository.findByIdWithLock(meetId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEET_NOT_FOUND));

        if (meet.getStatus() == MeetingStatus.ENDED) {
            throw new CustomException(ErrorCode.MEET_ALREADY_ENDED);
        }

        Participant participant = meetValidator.getParticipantOrThrow(meetId, userId);
        meetValidator.ensureParticipantJoined(participant);

        boolean isHost = meet.getHostId().equals(userId);

        //혼자만 남은 경우 - 즉시 종료
        if(meet.getParticipantCount() == 1){
            participant.leave();
            meet.decrementParticipantCount();
            meet.end();
            endMeet(meetId);

            eventPublisher.publishEvent(new ParticipantLeftEvent(
                    meet.getId(),
                    meet.getBoardId(),
                    userId
            ));
            return LeaveResponse.ended();
        }

        //호스트인 경우 - 위임 필요
        if(isHost){
            List<Long> candidateIds = participantRepository
                    .findActiveUserIdsByMeetId(meetId, userId);

            List<LeaveResponse.CandidateInfo> candidates = candidateIds.stream()
                    .map(id -> {
                        UserResponse userInfo = userClient.getUser(id);
                        return new LeaveResponse.CandidateInfo(id, userInfo.getNickname());
                    })
                    .toList();
            return LeaveResponse.transferRequired(candidates);
        }

        //일반 참가자 - 즉시 퇴장
        participant.leave();
        meet.decrementParticipantCount();

        eventPublisher.publishEvent(new ParticipantLeftEvent(
                meet.getId(),
                meet.getBoardId(),
                userId
        ));
        return LeaveResponse.left();
    }

    @Transactional
    public void endMeet(Long meetId){

        try{
            chatRedisTemplate.delete(ChatKeys.publicChat(meetId));
        }catch (Exception e){
            log.error("종료된 회의의 공개 채팅 히스토리를 삭제하는데 실패 meetId={}", meetId,e);
        }

        try {
            Set<String> dmUsers = stringRedis.opsForSet().members(ChatKeys.dmUsersIndex(meetId));
            if (dmUsers != null) {
                for (String uid : dmUsers) {
                    chatRedis.delete(ChatKeys.dmChat(meetId, Long.valueOf(uid)));
                }
            }
            stringRedis.delete(ChatKeys.dmUsersIndex(meetId));
        } catch (Exception e) {
            log.error("Failed to delete DM chats. meetId={}", meetId, e);
        }
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
    public void transferAndLeave(Long meetId, Long currentHostId, Long newHostId){
        Meet meet = meetRepository.findByIdWithLock(meetId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEET_NOT_FOUND));

        if(!meet.getHostId().equals(currentHostId)){
            throw new CustomException(ErrorCode.MEET_NOT_HOST);
        }

        Participant newHost = participantRepository.findByMeetIdAndUserId(meetId, newHostId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEET_NOT_PARTICIPANT));

        if(newHost.getStatus() != ParticipantStatus.JOINED){
            throw new CustomException(ErrorCode.PARTICIPANT_ALREADY_LEFT);
        }

        Participant oldHost = participantRepository.findByMeetIdAndUserId(meetId, currentHostId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEET_NOT_PARTICIPANT));

        meet.updateHost(newHostId);

        oldHost.leave();
        meet.decrementParticipantCount();

        eventPublisher.publishEvent(new ParticipantLeftEvent(
                meet.getId(),
                meet.getBoardId(),
                currentHostId
        ));

        UserResponse newHostInfo = userClient.getUser(newHostId);
        simpMessagingTemplate.convertAndSend(
                "/topic/meet/" + meetId + "/host",
                new HostChangedMessage(meetId, newHostId, newHostInfo.getNickname())
        );


    }

}
