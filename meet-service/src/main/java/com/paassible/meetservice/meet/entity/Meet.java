package com.paassible.meetservice.meet.entity;

import com.paassible.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Meet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long boardId;

    private Long hostId;

    @Enumerated(EnumType.STRING)
    private MeetingStatus status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    public static Meet create(Long boardId, Long hostId, LocalDateTime startTime) {
        Meet meet = new Meet();
        meet.boardId = boardId;
        meet.hostId = hostId;
        meet.startTime = startTime;
        meet.status = MeetingStatus.ONGOING;
        return meet;
    }

    public void end() {
        this.status = MeetingStatus.ENDED;
        this.endTime = LocalDateTime.now();
    }



}
