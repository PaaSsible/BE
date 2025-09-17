package com.paassible.recruitservice.post.repository;

import com.paassible.recruitservice.post.entity.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {
    void deleteByPostId(Long postId);

    @Query("select r from Recruitment r where r.postId = :postId")
    List<Recruitment> findByPostId(Long postId);

    List<Recruitment> findByPostIdIn(List<Long> postIds);
}
