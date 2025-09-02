package com.paassible.meetservice.meet.entity;

import com.paassible.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Participant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long meetId;

    private Long userId;

    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;

    @Enumerated(EnumType.STRING)
    private ParticipantStatus status;

    public void join() {
        this.status = ParticipantStatus.JOINED;
        this.joinedAt = LocalDateTime.now();
    }

    public void leave() {
        this.status = ParticipantStatus.LEFT;
        this.leftAt = LocalDateTime.now();
    }

    public void kick(){
        this.status = ParticipantStatus.KICKED;
        this.leftAt = LocalDateTime.now();
    }


}
