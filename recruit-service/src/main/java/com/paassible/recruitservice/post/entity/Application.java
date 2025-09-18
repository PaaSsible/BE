package com.paassible.recruitservice.post.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "applications")
@Getter
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;
    private Long recruitmentId;
    private Long userId;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private String message;

    protected Application() {}

    private Application(Long postId, Long recruitmentId, Long userId, String message) {
        this.postId = postId;
        this.recruitmentId = recruitmentId;
        this.userId = userId;
        this.message = message;
        this.status = ApplicationStatus.PENDING;
    }

    public static Application create(Long postId, Long recruitmentId, Long userId, String message) {
        return new Application(postId, recruitmentId, userId, message);
    }

    public void updateStatus(ApplicationStatus status) {
        this.status = status;
    }
}
