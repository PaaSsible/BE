package com.paassible.boardservice.comment.repository;

import com.paassible.boardservice.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTaskId(Long taskId);

    @Query("SELECT c FROM Comment c " +
            "JOIN Task t ON c.taskId = t.id " +
            "WHERE t.id = :taskId AND t.boardId = :boardId")
    List<Comment> findComments(Long taskId, Long boardId);

    @Query("SELECT c FROM Comment c " +
            "JOIN Task t ON c.taskId = t.id " +
            "WHERE c.id = :commentId AND t.id = :taskId AND t.boardId = :boardId")
    Optional<Comment> findComment(Long commentId, Long taskId, Long boardId);
}
