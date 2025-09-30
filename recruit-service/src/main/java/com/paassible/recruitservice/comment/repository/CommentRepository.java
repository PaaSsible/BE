package com.paassible.recruitservice.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.paassible.recruitservice.comment.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository< Comment, Long> {

    List<Comment> findByPostIdAndParentIdIsNull(Long postId);
    List<Comment> findByParentId(Long parentId);

}
