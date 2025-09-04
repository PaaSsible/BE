package com.paassible.boardservice.board.entity;

import com.paassible.boardservice.board.entity.enums.ProjectRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long boardId;

    @Enumerated(EnumType.STRING)
    private ProjectRole role;
}
