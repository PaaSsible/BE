package com.paassible.recruitservice.post.repository;

import com.paassible.recruitservice.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    Page<Post> findByWriterId(Long writerId, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p " +
            "JOIN Recruitment r ON p.id = r.postId " +
            "WHERE p.writerId = :writerId " +
            "AND r.positionId = :positionId")
    Page<Post> findMyPostsByPosition( Long writerId,
                                      Long positionId,
                                     Pageable pageable);



}

