package com.paassible.recruitservice.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.paassible.recruitservice.post.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository< Comment, Long> {

    List<Comment> findByPostIdAndParentIdIsNull(Long postId);
    List<Comment> findByParentId(Long parentId);

    Optional<Comment> findByIdAndWriterId(Long id, Long writerId);
}
