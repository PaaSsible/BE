package com.paassible.meetservice.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MeetNotificationPublisher {

    private final NotificationPublisher publisher;

    public void startMeet(Long memberId, String applicantName, String title) {
        publisher.sendNotification(
                memberId,
                "MEET",
                "새로운 지원이 도착했어요!",
                applicantName + "님이 [" + title + "]에 지원서를 제출했어요.\n함께할 팀원을 검토해 보세요"
        );
    }

    public void sendAccepted(Long applicantId, String title) {
        publisher.sendNotification(
                applicantId,
                "RECRUIT",
                "지원하신 프로젝트의 결과가 도착했어요.",
                "[" + title + "] 팀에서 함께하고 싶다는 소식을 전했어요!\n팀 페이지에서 포지션을 선택하고 작업을 시작해 보세요."
        );
    }

    public void sendRejected(Long applicantId, String title) {
        publisher.sendNotification(
                applicantId,
                "RECRUIT",
                "지원하신 프로젝트의 결과가 도착했어요.",
                "아쉽지만 ["+ title +"] 팀과는 이번에 함께하지 못하게 되었어요.\n팀의 결정 사유를 확인해 주세요."
        );
    }
}

