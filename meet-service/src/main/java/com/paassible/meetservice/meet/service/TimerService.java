package com.paassible.meetservice.meet.service;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import com.paassible.meetservice.meet.entity.Meet;
import com.paassible.meetservice.meet.message.TimerMessage;
import com.paassible.meetservice.meet.message.TimerType;
import com.paassible.meetservice.meet.repository.MeetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimerService {

    private final MeetValidator meetValidator;
    private final SimpMessagingTemplate messagingTemplate;

    private String now() {
        return DateTimeFormatter.ISO_INSTANT.format(Instant.now());
    }

    private void sendErrorToUser(Long userId, String message) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/errors",
                message
        );
    }

    private void sendHostOnlyError(Long userId) {
        sendErrorToUser(userId, ErrorCode.MEET_HOST_ONLY.getMessage());
    }

    @Transactional
    public void startTimer(Long meetId, Long userId, Integer duration) {
        Meet meet = meetValidator.validateMeetOngoing(meetId);

        if (!meet.getHostId().equals(userId)) {
            sendHostOnlyError(userId);
            return;
        }

        try {
            meet.startTimer();
        } catch (CustomException | IllegalStateException e) {
            sendErrorToUser(userId, e.getMessage());
            return;
        }

        messagingTemplate.convertAndSend(
                "/topic/meet/" + meetId + "/timer",
                new TimerMessage(TimerType.START, duration, now())
        );
    }

    @Transactional
    public void pauseTimer(Long meetId, Long userId) {
        Meet meet = meetValidator.validateMeetOngoing(meetId);
        if (!meet.getHostId().equals(userId)) {
            sendHostOnlyError(userId);
            return;
        }

        try {
            meet.stopTimer();
        } catch (CustomException | IllegalStateException e) {
            sendErrorToUser(userId, e.getMessage());
            return;
        }

        messagingTemplate.convertAndSend(
                "/topic/meet/" + meetId + "/timer",
                new TimerMessage(TimerType.PAUSE, 0, now())
        );
    }

    @Transactional
    public void resumeTimer(Long meetId, Long userId) {
        Meet meet = meetValidator.validateMeetOngoing(meetId);
        if (!meet.getHostId().equals(userId)) {
            sendHostOnlyError(userId);
            return;
        }

        try {
            meet.startTimer();
        } catch (CustomException | IllegalStateException e) {
            sendErrorToUser(userId, e.getMessage());
            return;
        }

        messagingTemplate.convertAndSend(
                "/topic/meet/" + meetId + "/timer",
                new TimerMessage(TimerType.RESUME, 0, now())
        );
    }

    @Transactional
    public void endTimer(Long meetId, Long userId) {
        Meet meet = meetValidator.validateMeetOngoing(meetId);
        if (!meet.getHostId().equals(userId)) {
            sendHostOnlyError(userId);
            return;
        }

        try {
            meet.stopTimer();
        } catch (CustomException | IllegalStateException e) {
            sendErrorToUser(userId, e.getMessage());
            return;
        }

        messagingTemplate.convertAndSend(
                "/topic/meet/" + meetId + "/timer",
                new TimerMessage(TimerType.END, 0, now())
        );
    }
}
