package com.paassible.recruitservice.application.dto;


import com.paassible.recruitservice.application.entity.Application;

public record ApplicantResponse(
       Long id,
       Long postId,
       Long applicantId

) {
    public static ApplicantResponse from (Application application){
        return new ApplicantResponse(
                application.getId(),
                application.getPostId(),
                application.getApplicantId()
        );
    }
}
