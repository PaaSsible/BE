package com.paassible.userservice.user.entity;

import com.paassible.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Portfolio extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long positionId;

    private String title;

    private String summary;

    private String description;

    public void updateInfo(Long positionId, String title, String summary, String description) {
        this.positionId = positionId;
        this.title = title;
        this.summary = summary;
        this.description = description;
    }
}
