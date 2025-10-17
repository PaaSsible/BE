package com.paassible.meetservice.meet.service;

import com.paassible.meetservice.client.board.BoardClient;
import com.paassible.meetservice.client.board.BoardMemberResponse;
import com.paassible.meetservice.meet.dto.ParticipantStatusMessage;
import com.paassible.meetservice.meet.entity.Meet;
import com.paassible.meetservice.meet.event.ParticipantLeftEvent;
import com.paassible.meetservice.meet.event.ParticipantJoinedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class MeetCacheEventHandler {

    private final RedisTemplate<String,String> redisTemplate;
    private final BoardClient boardClient;
    private final SimpMessagingTemplate simpMessagingTemplate;


    private String participantsKey(Long meetId) {

        return "meeting:" + meetId + ":participants";
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleParticipantJoined (ParticipantJoinedEvent event) {
        String key = participantsKey(event.meet().getId());
        try{
            redisTemplate.opsForSet().add(key, event.userId().toString());
            broadcastCurrentStatus(event.meet());
        }catch(Exception e){
            log.warn("Failed to update Redis on join (meetId={}, userId={})",
                    event.meet().getId(), event.userId(), e);
        }
    }

@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleParticipantLeft(ParticipantLeftEvent event) {
        String key = participantsKey(event.meet().getId());
        try{
            redisTemplate.opsForSet().remove(key, event.userId().toString());
            Long size = redisTemplate.opsForSet().size(key);
            if(size != null && size ==0){
                redisTemplate.delete(key);
            }
            broadcastCurrentStatus(event.meet());
        }catch(Exception e){
            log.warn("Failed to update Redis on leave (meetId={}, userId={})",
                    event.meet().getId(), event.userId(), e);
        }
    }


    private void broadcastCurrentStatus(Meet meet) {

        try {
            List<BoardMemberResponse> allMembers = boardClient.getBoardMembers(meet.getBoardId());
            Set<String> redisMembers =
                    redisTemplate.opsForSet().members("meeting:" + meet.getId() + ":participants");

            List<Long> joinedIds = redisMembers == null
                    ? List.of()
                    : redisMembers.stream().map(Long::valueOf).toList();


            List<BoardMemberResponse> presentMembers = allMembers.stream()
                    .filter(member -> joinedIds.contains(member.getUserId()))
                    .toList();


            List<BoardMemberResponse> absentMembers = allMembers.stream()
                    .filter(member -> !joinedIds.contains(member.getUserId()))
                    .sorted(Comparator.comparing(BoardMemberResponse::getUserName))
                    .toList();

            broadcastParticipantStatus(meet.getId(), presentMembers, absentMembers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void broadcastParticipantStatus(Long meetId, List<BoardMemberResponse> presentMembers, List<BoardMemberResponse> absentMembers) {
        ParticipantStatusMessage message = new ParticipantStatusMessage(presentMembers,absentMembers);
        simpMessagingTemplate.convertAndSend("/topic/meet/"+meetId+"/status",message);
    }


}
