package com.paassible.recruitservice.application.repository;

import com.paassible.recruitservice.application.entity.Application;
import com.paassible.recruitservice.application.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicantionRepository extends JpaRepository<Application,Long> {
    boolean existsByPostIdAndApplicantId(Long postId, Long applicantId);
    List<Application> findAllByPostIdAndStatus(Long postId, ApplicationStatus status);
    @Query("SELECT a FROM Application a WHERE a.applicantId = :userId ORDER BY a.updatedAt DESC")
    List<Application> findAllByApplicantIdOrderByUpdatedAtDesc(@Param("userId") Long userId);
    Optional<Application> findByPostIdAndApplicantId(Long postId, Long applicantId);
}


