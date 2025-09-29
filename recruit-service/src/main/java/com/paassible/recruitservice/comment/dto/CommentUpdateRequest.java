package com.paassible.recruitservice.comment.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentUpdateRequest(
   @NotBlank
   String content
) {}
