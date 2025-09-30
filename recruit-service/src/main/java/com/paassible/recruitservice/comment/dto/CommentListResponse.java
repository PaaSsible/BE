package com.paassible.recruitservice.comment.dto;

import java.util.List;

public record CommentListResponse(
        int commentCount,
        List<CommentResponse> comments
) {}

