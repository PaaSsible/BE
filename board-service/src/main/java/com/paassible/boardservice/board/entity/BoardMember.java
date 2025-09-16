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

    @Enumerated(EnumType.STRING)
    private ProjectRole role;

    @Enumerated(EnumType.STRING)
    private MemberStatus status = MemberStatus.ACTIVE;

    public void updateMemberStatus(MemberStatus status) {
        this.status = status;
    }
}
