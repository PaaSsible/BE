package com.paassible.meetservice.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MeetNotificationPublisher {

    private final NotificationPublisher publisher;

    public void notifyMeetStarted(Long memberId, String boardName) {
        publisher.sendNotification(
                memberId,
                "MEET",
                "회의가 시작됐어요! 지금 바로 참여하세요.",
                "["+boardName + "] 팀의 온라인 회의에 초대 요청을 받았습니다.\n팀원들과의 논의를 놓치지 않도록 지금 입장해 보세요."
        );
    }



}

