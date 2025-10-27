package com.paassible.meetservice.meet.service;

import com.paassible.meetservice.util.MeetKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpeakingTrackerService {

    @Qualifier("stringRedis")
    private final StringRedisTemplate stringRedis;

    private final SimpMessagingTemplate messaging;

    // 튜닝 파라미터
    private static final long TIMEOUT_MS = 8_000L;      // 워치독 타임아웃
    private static final long SILENT_MS  = 30_000L;     // 무발언 기준(30초)
    private static final int  SCAN_COUNT = 200;         // SCAN
    private static final long WATCHDOG_FIXED_RATE = 10_000L; // 10초
    private static final long SNAPSHOT_FIXED_RATE = 10_000L; // 10초


    public void updateSpeaking(Long meetId, Long userId, boolean speaking) {
        long now = System.currentTimeMillis();
        String speakKey = MeetKeys.speaking(meetId);
        String lastKey  = MeetKeys.lastSpokeAt(meetId);

        if (speaking) {

            stringRedis.opsForHash().put(speakKey, userId.toString(), Long.toString(now));

            stringRedis.opsForZSet().add(lastKey, userId.toString(), (double) now);
        } else {

            messaging.convertAndSend("/topic/meet/" + meetId + "/inactive", Map.of("userId", userId));

            stringRedis.opsForHash().delete(speakKey, userId.toString());

            stringRedis.opsForZSet().add(lastKey, userId.toString(), (double) now);
        }
    }


    @Scheduled(fixedRate = WATCHDOG_FIXED_RATE)
    public void watchdogTimeoutCloser() {
        long now = System.currentTimeMillis();

        scanKeys("meeting:*:speaking", key -> {
            Map<Object, Object> entries = stringRedis.opsForHash().entries(key);
            if (entries == null || entries.isEmpty()) return;

            String[] parts = key.split(":");
            if (parts.length < 3) return;
            long meetId = Long.parseLong(parts[1]);

            String lastZ = MeetKeys.lastSpokeAt(meetId);

            for (Map.Entry<Object, Object> e : entries.entrySet()) {
                long userId = Long.parseLong(e.getKey().toString());
                long lastTrue = Long.parseLong(e.getValue().toString());

                if ((now - lastTrue) > TIMEOUT_MS) {
                    messaging.convertAndSend("/topic/meet/" + meetId + "/inactive", Map.of("userId", userId));
                    stringRedis.opsForHash().delete(key, e.getKey());
                    stringRedis.opsForZSet().add(lastZ, Long.toString(userId), (double) lastTrue);
                }
            }
        });
    }


    @Scheduled(fixedRate = SNAPSHOT_FIXED_RATE)
    public void broadcastSilentSnapshot() {
        long now = System.currentTimeMillis();
        long cutoff = now - SILENT_MS;

        scanKeys(MeetKeys.lastSpokeAtAnyPattern(), lastKey -> {


            String[] parts = lastKey.split(":");
            if (parts.length < 3) return;
            long meetId = Long.parseLong(parts[1]);

            Set<String> participants = stringRedis.opsForSet().members(MeetKeys.participants(meetId));
            if (participants == null || participants.isEmpty()) return;


            Set<ZSetOperations.TypedTuple<String>> tuples =
                    stringRedis.opsForZSet().rangeByScoreWithScores(lastKey, Double.NEGATIVE_INFINITY, cutoff);
            if (tuples == null || tuples.isEmpty()) {
                clearSilentIfNeeded(meetId);
                return;
            }


            Set<Long> silent = new java.util.HashSet<>();
            for (ZSetOperations.TypedTuple<String> t : tuples) {
                String uid = t.getValue();
                if (uid != null && participants.contains(uid)) {
                    silent.add(Long.valueOf(uid));
                }
            }


            if (updateSilentSetIfChanged(meetId, silent)) {
                messaging.convertAndSend(
                        "/topic/meet/" + meetId + "/silent",
                        Map.of(
                                "silentUserIds", silent,
                                "thresholdSec", (int) (SILENT_MS / 1000),
                                "snapshotAt", java.time.Instant.ofEpochMilli(now).toString()
                        )
                );
            }
        });
    }


    private boolean updateSilentSetIfChanged(long meetId, Set<Long> newSilent) {
        String key = MeetKeys.silentSet(meetId);
        Set<String> prev = stringRedis.opsForSet().members(key);
        Set<String> newSet = new java.util.HashSet<>();
        for (Long id : newSilent) newSet.add(id.toString());

        boolean changed = (prev == null) || !prev.equals(newSet);
        if (changed) {
            stringRedis.delete(key);
            if (!newSet.isEmpty()) {
                stringRedis.opsForSet().add(key, newSet.toArray(new String[0]));
            }
        }
        return changed;
    }

    private void clearSilentIfNeeded(long meetId) {
        String key = MeetKeys.silentSet(meetId);
        Set<String> prev = stringRedis.opsForSet().members(key);
        if (prev != null && !prev.isEmpty()) {
            stringRedis.delete(key);
            messaging.convertAndSend(
                    "/topic/meet/" + meetId + "/silent",
                    Map.of(
                            "silentUserIds", java.util.List.of(),
                            "thresholdSec", (int) (SILENT_MS / 1000),
                            "snapshotAt", java.time.Instant.now().toString()
                    )
            );
        }
    }

    private void scanKeys(String pattern, java.util.function.Consumer<String> onKey) {
        try {
            stringRedis.execute(connection -> {
                var rawConn = (org.springframework.data.redis.connection.RedisConnection) connection;
                var options = org.springframework.data.redis.core.ScanOptions.scanOptions()
                        .match(pattern)
                        .count(SCAN_COUNT)
                        .build();
                try (var cursor = rawConn.scan(options)) {
                    while (cursor.hasNext()) {
                        byte[] raw = cursor.next();
                        String key = (String) stringRedis.getKeySerializer().deserialize(raw);
                        if (key != null) onKey.accept(key);
                    }
                }
                return null;
            }, true);
        } catch (Exception e) {
            log.warn("SCAN failed (pattern={}): {}", pattern, e.getMessage(), e);
        }
    }
}
