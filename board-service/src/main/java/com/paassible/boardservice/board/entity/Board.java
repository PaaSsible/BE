package com.paassible.boardservice.board.entity;

import com.paassible.boardservice.board.entity.enums.ActivityType;
import com.paassible.boardservice.board.entity.enums.BoardStatus;
import com.paassible.boardservice.board.entity.enums.DetailType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String content;

    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    @Enumerated(EnumType.STRING)
    private DetailType detailType;

    @Enumerated(EnumType.STRING)
    private BoardStatus status;

    public void updateBoard(String name, String content, ActivityType activityType, DetailType detailType) {
        this.name = name;
        this.content = content;
        this.activityType = activityType;
        this.detailType = detailType;
    }
}
