package com.paassible.boardservice.task.service;

import com.paassible.boardservice.board.service.UserBoardService;
import com.paassible.boardservice.client.UserClient;
import com.paassible.boardservice.client.UserResponse;
import com.paassible.boardservice.task.dto.CommentRequest;
import com.paassible.boardservice.task.dto.CommentResponse;
import com.paassible.boardservice.task.entity.Comment;
import com.paassible.boardservice.task.exception.TaskException;
import com.paassible.boardservice.task.repository.CommentRepository;
import com.paassible.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final UserBoardService userBoardService;
    private final CommentRepository commentRepository;
    private final UserClient userClient;

    public void createComment(Long userId, Long boardId, Long taskId, CommentRequest request) {
        userBoardService.validateUserInBoard(boardId, userId);

        Comment comment = Comment.builder()
                .taskId(taskId)
                .userId(userId)
                .content(request.getComment())
                .build();
        commentRepository.save(comment);
    }

    @Transactional
    public void updateComment(Long userId, Long boardId, Long taskId, Long commentId, CommentRequest request) {
        userBoardService.validateUserInBoard(boardId, userId);

        Comment comment = commentRepository.findComment(commentId, taskId, boardId)
                .orElseThrow(() -> new TaskException(ErrorCode.TASK_COMMENT_NOT_FOUND));

        if (!comment.getUserId().equals(userId)) {
            throw new TaskException(ErrorCode.COMMENT_NOT_OWNER);
        }

        comment.updateContent(request.getComment());
    }

    public void deleteComment(Long userId, Long boardId, Long taskId, Long commentId) {
        userBoardService.validateUserInBoard(boardId, userId);

        Comment comment = commentRepository.findComment(commentId, taskId, boardId)
                .orElseThrow(() -> new TaskException(ErrorCode.TASK_COMMENT_NOT_FOUND));

        if (!comment.getUserId().equals(userId)) {
            throw new TaskException(ErrorCode.COMMENT_NOT_OWNER);
        }

        commentRepository.delete(comment);
    }

    public List<CommentResponse> getCommentsByTask(Long userId, Long boardId, Long taskId) {
        userBoardService.validateUserInBoard(boardId, userId);

        return commentRepository.findComments(taskId, boardId).stream()
                .map(comment -> {
                    UserResponse user = userClient.getUser(comment.getUserId());
                    return CommentResponse.from(comment, user);
                })
                .toList();
    }
}