package com.paassible.boardservice.board.entity;

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

    private String activityType;

    private String detailType;

    @Enumerated(EnumType.STRING)
    private BoardStatus status;
}
