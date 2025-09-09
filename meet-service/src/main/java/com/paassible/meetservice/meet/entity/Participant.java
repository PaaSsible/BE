package com.paassible.meetservice.meet.entity;

import com.paassible.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@Getter
public class Participant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long meetId;

    private Long userId;

    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;

    private Long totalStayDuration = 0L;

    @Enumerated(EnumType.STRING)
    private ParticipantStatus status;

    public static Participant create(Long meetId, Long userId) {
        Participant participant = new Participant();
        participant.meetId = meetId;
        participant.userId = userId;
        participant.status = ParticipantStatus.JOINED;
        participant.joinedAt = LocalDateTime.now();
        return participant;
    }

    public void rejoin(){
        this.status = ParticipantStatus.JOINED;
        this.joinedAt = LocalDateTime.now();
        this.leftAt = null;
    }

    public void leave() {
        long sessionDuration = Duration.between(joinedAt, LocalDateTime.now()).getSeconds();
        this.totalStayDuration += sessionDuration;
        this.status = ParticipantStatus.LEFT;
        this.leftAt = LocalDateTime.now();
    }



}
