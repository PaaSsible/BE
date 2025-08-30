package com.paassible.recruitservice.post.repository;

import com.paassible.recruitservice.post.dto.PostDetailResponse;
import com.paassible.recruitservice.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("select new com.paassible.recruitservice.post.dto.PostDetailResponse(" +
            "p.id, p.title, p.content, p.deadline, p.months, p.writerId, null) " +
            "from Post p where p.id = :postId")
    Optional<PostDetailResponse> findPostDetail(Long postId);
}
