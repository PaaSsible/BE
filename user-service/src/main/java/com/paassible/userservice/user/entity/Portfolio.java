package com.paassible.userservice.user.entity;

import com.paassible.common.entity.BaseEntity;
import com.paassible.userservice.user.entity.enums.MainCategory;
import com.paassible.userservice.user.entity.enums.SubCategory;
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

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private MainCategory mainCategory;

    @Enumerated(EnumType.STRING)
    private SubCategory subCategory;

    private int contribution;

    private String image;

    private boolean generatedByAi;

    public void updateInfo(Long positionId, String title, String summary, String description, String image) {
        this.positionId = positionId;
        this.title = title;
        this.summary = summary;
        this.description = description;
        this.image = image;
    }
}
