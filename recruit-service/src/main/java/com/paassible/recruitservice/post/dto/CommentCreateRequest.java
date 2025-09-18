package com.paassible.recruitservice.post.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequest(
        @NotBlank
        String content,
        Long parentId
) {}

