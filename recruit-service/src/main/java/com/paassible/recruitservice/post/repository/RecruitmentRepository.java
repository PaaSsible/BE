package com.paassible.recruitservice.post.repository;

import com.paassible.recruitservice.post.entity.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {
}
