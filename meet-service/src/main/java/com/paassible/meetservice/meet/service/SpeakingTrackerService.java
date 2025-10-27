package com.paassible.meetservice.meet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class SpeakingTrackerService {

    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String HASH_PREFIX = "meeting:speaking:";

    public void updateSpeaking(Long meetId, Long userId, boolean speaking) {
        String key = HASH_PREFIX + meetId;

        if (speaking) {
            redisTemplate.opsForHash().put(key, userId.toString(), String.valueOf(Instant.now().toEpochMilli()));
        }
    }

    @Scheduled(fixedRate = 10000)
    public void detectInactiveUsers() {
        Instant now = Instant.now();

        // 모든 회의 키 조회
        Set<String> keys = redisTemplate.keys(HASH_PREFIX + "*");
        if (keys == null) return;

        for (String key : keys) {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
            Long meetId = Long.valueOf(key.substring(HASH_PREFIX.length()));

            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                Long userId = Long.valueOf(entry.getKey().toString());
                Instant lastSpeak = Instant.ofEpochMilli(Long.parseLong(entry.getValue().toString()));

                if (Duration.between(lastSpeak, now).toSeconds() > 30) {

                    messagingTemplate.convertAndSend(
                            "/topic/meet/" + meetId + "/inactive",
                            Map.of("userId", userId)
                    );

                    // 감지 후 삭제
                    redisTemplate.opsForHash().delete(key, entry.getKey());
                }
            }
        }
    }
}
