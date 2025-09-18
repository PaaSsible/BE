package com.paassible.recruitservice.post.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentUpdateRequest(
   @NotBlank
   String content
) {}
