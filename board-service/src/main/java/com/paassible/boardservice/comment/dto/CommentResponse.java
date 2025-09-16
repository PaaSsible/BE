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

    public static CommentResponse from(Comment comment, Long userId, String userName, String profileImageUrl) {
        return CommentResponse.builder()
                .id(comment.getId())
                .taskId(comment.getTaskId())
                .userId(userId)
                .userName(userName)
                .profileImageUrl(profileImageUrl)
                .comment(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
