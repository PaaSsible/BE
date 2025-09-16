package com.paassible.boardservice.comment.service;

import com.paassible.boardservice.board.entity.BoardMember;
import com.paassible.boardservice.board.entity.enums.MemberStatus;
import com.paassible.boardservice.board.service.BoardMemberService;
import com.paassible.boardservice.client.UserClient;
import com.paassible.boardservice.client.UserResponse;
import com.paassible.boardservice.comment.dto.CommentRequest;
import com.paassible.boardservice.comment.dto.CommentResponse;
import com.paassible.boardservice.comment.entity.Comment;
import com.paassible.boardservice.task.entity.Task;
import com.paassible.boardservice.task.exception.TaskException;
import com.paassible.boardservice.comment.repository.CommentRepository;
import com.paassible.boardservice.task.service.TaskService;
import com.paassible.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final BoardMemberService userBoardService;
    private final TaskService taskService;
    private final BoardMemberService boardMemberService;

    private final CommentRepository commentRepository;
    private final UserClient userClient;

    public void createComment(Long userId, Long boardId, Long taskId, CommentRequest request) {
        userBoardService.validateUserInBoard(boardId, userId);

        Task task = taskService.getTask(taskId);
        if (!task.getBoardId().equals(boardId)) {
            throw new TaskException(ErrorCode.BOARD_TASK_NOT_FOUND);
        }

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
                    BoardMember boardMember = boardMemberService.getBoardMember(comment.getUserId(), boardId);

                    String userName;
                    String profileImageUrl;
                    if (boardMember.getStatus() == MemberStatus.INACTIVE) {
                        userName = "알 수 없음";
                        profileImageUrl = "default";
                    } else {
                        UserResponse user = userClient.getUser(comment.getUserId());
                        userName = user.getNickname();
                        profileImageUrl = user.getProfileImageUrl();
                    }
                    return CommentResponse.from(comment, comment.getUserId(), userName, profileImageUrl);
                })
                .toList();
    }
}