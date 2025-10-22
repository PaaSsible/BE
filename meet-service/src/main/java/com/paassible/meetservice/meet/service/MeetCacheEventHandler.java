package com.paassible.meetservice.meet.service;

import com.paassible.meetservice.meet.event.ParticipantLeftEvent;
import com.paassible.meetservice.meet.event.ParticipantJoinedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class MeetCacheEventHandler {

    private final RedisTemplate<String,String> redisTemplate;

    private String participantsKey(Long meetId) {

        return "meeting:" + meetId + ":participants";
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleParticipantJoined (ParticipantJoinedEvent event) {
        String key = participantsKey(event.meetId());
        try{
            redisTemplate.opsForSet().add(key, event.userId().toString());
        }catch(Exception e){
            log.warn("Failed to update Redis on join (meetId={}, userId={})",
                    event.meetId(), event.userId(), e);
        }
    }

@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleParticipantLeft(ParticipantLeftEvent event) {
        try{
            String key = participantsKey(event.meetId());
            redisTemplate.opsForSet().remove(key, event.userId().toString());
            Long size = redisTemplate.opsForSet().size(key);
            if(size != null && size ==0){
                redisTemplate.delete(key);
            }
        }catch(Exception e){
            log.warn("Failed to update Redis on leave (meetId={}, userId={})",
                    event.meetId(), event.userId(), e);
        }
    }

}
