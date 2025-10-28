package com.paassible.recruitservice.application.dto;

public record RejectReasonResponse(
        Long applicationId,
        String rejectReason
) {
    public static RejectReasonResponse from(Long id, String reason) {
        return new RejectReasonResponse(id, reason);
    }
}
