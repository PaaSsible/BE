package com.paassible.boardservice.task.dto;

import com.paassible.boardservice.client.UserResponse;
import com.paassible.boardservice.task.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {
    private Long id;
    private Long taskId;
    private Long userId;
    private String userName;
    private String comment;
    private LocalDateTime createdAt;

    public static CommentResponse from(Comment comment, UserResponse user) {
        return CommentResponse.builder()
                .id(comment.getId())
                .taskId(comment.getTaskId())
                .userId(user.getId())
                .userName(user.getNickname())
                .comment(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
