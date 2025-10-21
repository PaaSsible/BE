package com.paassible.recruitservice.comment.dto;

import java.util.List;

public record CommentListResponse(
        Long currentUserId,
        int commentCount,
        List<CommentResponse> comments
) {}

