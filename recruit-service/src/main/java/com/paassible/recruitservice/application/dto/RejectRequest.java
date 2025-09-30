package com.paassible.recruitservice.application.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectRequest(
        @NotBlank String rejectReason
){}
