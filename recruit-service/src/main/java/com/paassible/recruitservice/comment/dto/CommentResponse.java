package com.paassible.recruitservice.comment.dto;

import com.paassible.recruitservice.comment.entity.Comment;

import java.util.List;

public record CommentResponse(
        Long id,
        String content,
        Long writerId,
        boolean deleted,
        List<CommentResponse> children
) {
    public static CommentResponse from(Comment comment, List<CommentResponse> children) {
        return new CommentResponse(
                comment.getId(),
                comment.isDeleted() ? null : comment.getContent(),
                comment.getWriterId(),
                comment.isDeleted(),
                children
        );
    }
}


