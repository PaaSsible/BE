package com.paassible.recruitservice.application.entity;

import com.paassible.recruitservice.application.dto.RejectRequest;
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
    private Long applicantId;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private String rejectReason;

    protected Application() {}

    private Application(Long postId, Long applicantId) {
        this.postId = postId;
        this.applicantId = applicantId;
        this.status = ApplicationStatus.PENDING;
    }

    public static Application create(Long postId, Long userId) {
        return new Application(postId, userId);
    }

    public void updateStatus(ApplicationStatus status) {
        this.status = status;
    }

    public void accept() {
        this.status = ApplicationStatus.ACCEPTED;
    }

    public void reject(RejectRequest reason) {
        this.status = ApplicationStatus.REJECTED;
        this.rejectReason = reason.rejectReason();
    }

}
