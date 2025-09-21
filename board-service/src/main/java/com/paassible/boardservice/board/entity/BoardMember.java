package com.paassible.boardservice.board.entity;

import com.paassible.boardservice.board.entity.enums.ActivityType;
import com.paassible.boardservice.board.entity.enums.DetailType;
import com.paassible.boardservice.board.entity.enums.MemberStatus;
import com.paassible.boardservice.board.entity.enums.ProjectRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BoardMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long boardId;

    private Long positionId;

    @Enumerated(EnumType.STRING)
    private ProjectRole role;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private MemberStatus status = MemberStatus.ACTIVE;

    public void updatePosition(Long positionId) {
        this.positionId = positionId;
    }

    public void updateMemberStatus(MemberStatus status) {
        this.status = status;
    }
}
