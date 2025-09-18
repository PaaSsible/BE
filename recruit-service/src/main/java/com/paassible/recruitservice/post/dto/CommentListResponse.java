package com.paassible.recruitservice.post.dto;

import java.util.List;

public record CommentListResponse(
        int commentCount,
        List<CommentResponse> comments
) {}

