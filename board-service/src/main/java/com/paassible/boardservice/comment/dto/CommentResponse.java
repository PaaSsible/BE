package com.paassible.boardservice.comment.dto;

import com.paassible.boardservice.client.UserResponse;
import com.paassible.boardservice.comment.entity.Comment;
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
    private String profileImageUrl;
    private String comment;
    private LocalDateTime createdAt;

    public static CommentResponse from(Comment comment, UserResponse user) {
        return CommentResponse.builder()
                .id(comment.getId())
                .taskId(comment.getTaskId())
                .userId(user.getId())
                .userName(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .comment(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
