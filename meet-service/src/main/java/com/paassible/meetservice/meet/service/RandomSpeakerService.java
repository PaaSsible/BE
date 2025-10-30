package com.paassible.meetservice.meet.service;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import com.paassible.meetservice.meet.entity.Meet;
import com.paassible.meetservice.meet.message.RandomPickMessage;
import com.paassible.meetservice.util.MeetKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class RandomSpeakerService {

    private final StringRedisTemplate stringRedis;
    private final SimpMessagingTemplate messaging;
    private final MeetValidator meetValidator;

    private static final double B_IDLE = 0.10;
    private static final int    IDLE_CAP_MIN = 30;
    private static final double W_MIN = 0.5;
    private static final double W_MAX = 5.0;

    public void pickAndBroadcast(long meetId, Long userId) {

        Meet meet = meetValidator.validateMeetOngoing(meetId);

        if (!meet.getHostId().equals(userId)) {
            throw new CustomException(ErrorCode.MEET_NOT_HOST);
        }

        long now = System.currentTimeMillis();

        Set<String> participants = stringRedis.opsForSet().members(MeetKeys.participants(meetId));
        if (participants == null || participants.isEmpty()) {
            return;
        }

        String lastPicked = stringRedis.opsForValue().get(MeetKeys.lastPicked(meetId));

        String lastKey = MeetKeys.lastSpokeAt(meetId);
        Set<ZSetOperations.TypedTuple<String>> tuples =
                stringRedis.opsForZSet().rangeWithScores(lastKey, 0, -1);

        Map<String, Long> lastMap = new HashMap<>();
        if (tuples != null) {
            for (var t : tuples) {
                if (t.getValue() != null && t.getScore() != null) {
                    lastMap.put(t.getValue(), t.getScore().longValue());
                }
            }
        }

        class C { String s; long uid; double w; C(String s,long uid,double w){this.s=s;this.uid=uid;this.w=w;} }
        List<C> pool = new ArrayList<>();

        for (String s : participants) {
            if (lastPicked != null && lastPicked.equals(s) && participants.size() > 1) {
                continue;
            }

            try {
                long uid = Long.parseLong(s);
                Long last = lastMap.get(s);
                double idleMin = (last == null) ? IDLE_CAP_MIN :
                        Math.min((now - last) / 60_000.0, IDLE_CAP_MIN);

                double w = 1.0 + B_IDLE * idleMin;
                w = Math.max(W_MIN, Math.min(W_MAX, w));
                pool.add(new C(s, uid, w));
            } catch (NumberFormatException e) {
                log.warn("Invalid userId format: {}", s);
            }
        }

        if (pool.isEmpty()) {
            return;
        }

        double total = pool.stream().mapToDouble(c -> c.w).sum();
        double r = ThreadLocalRandom.current().nextDouble(total);

        double acc = 0;
        C picked = pool.get(pool.size() - 1);
        for (var c : pool) {
            acc += c.w;
            if (r <= acc) {
                picked = c;
                break;
            }
        }

        stringRedis.opsForValue().set(
                MeetKeys.lastPicked(meetId),
                Long.toString(picked.uid)
        );

        messaging.convertAndSend(
                "/topic/meet/" + meetId + "/random-pick",
                new RandomPickMessage(
                        picked.uid,
                        java.time.Instant.ofEpochMilli(now).toString()
                )
        );
    }
}