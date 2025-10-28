package com.paassible.recruitservice.application.dto;

import com.paassible.recruitservice.application.entity.Application;
import com.paassible.recruitservice.client.UserResponse;

import java.util.List;


public record ApplicantResponse(
        Long id,
        Long postId,
        Long applicantId,
        String applicantName,
        String university,
        String major,
        String positionName,
        List<String>stackNames
) {
    public static ApplicantResponse from(Application application, UserResponse user) {
        return new ApplicantResponse(
                application.getId(),
                application.getPostId(),
                application.getApplicantId(),
                user.getNickname(),
                user.getUniversity(),
                user.getMajor(),
                user.getPositionName(),
                user.getStackNames()
        );
    }
}
