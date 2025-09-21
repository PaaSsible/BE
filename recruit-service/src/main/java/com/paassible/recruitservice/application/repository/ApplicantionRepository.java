package com.paassible.recruitservice.application.repository;

import com.paassible.recruitservice.application.entity.Application;
import com.paassible.recruitservice.application.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicantionRepository extends JpaRepository<Application,Long> {
    boolean existsByPostIdAndApplicantId(Long postId, Long applicantId);
    List<Application> findAllByPostIdAndStatus(Long postId, ApplicationStatus status);
}
