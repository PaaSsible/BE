package com.paassible.recruitservice.post.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "recruitment")
public class Recruitment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recruitmentId;

    private Long postId;
    private Long positionId;
    private Long stackId;

    protected Recruitment() {}

    private Recruitment(Long postId, Long positionId, Long stackId) {
        this.postId = postId;
        this.positionId = positionId;
        this.stackId = stackId;
    }

    public static Recruitment create(Long postId, Long positionId, Long stackId) {
        return new Recruitment(postId, positionId, stackId);
    }

    public void update(Long positionId, Long stackId) {
        this.positionId = positionId;
        this.stackId = stackId;
    }
}

