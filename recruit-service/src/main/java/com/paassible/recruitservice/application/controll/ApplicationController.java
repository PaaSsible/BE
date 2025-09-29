package com.paassible.recruitservice.application.controll;

import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import com.paassible.recruitservice.application.dto.AcceptRequest;
import com.paassible.recruitservice.application.dto.ApplicantResponse;
import com.paassible.recruitservice.application.dto.RejectRequest;
import com.paassible.recruitservice.application.service.ApplicantionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recruits")
@RequiredArgsConstructor
@Tag(name = "팀원 모집 지원 API", description = "팀원 모집 지원, 조회, 수락, 거절")
public class ApplicationController {

    private final ApplicantionService applicationService;

    @Operation(summary = "지원하기")
    @PostMapping("/{postId}/applications")
    public ApiResponse<Void> apply(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserJwtDto user){
        applicationService.apply(postId, user.getUserId());
        return ApiResponse.success(SuccessCode.CREATED);
    }

    @Operation(summary = "지원 조회")
    @GetMapping("/{postId}/applications")
    public ApiResponse<List<ApplicantResponse>> getApplicants(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserJwtDto user){
        List<ApplicantResponse> response = applicationService.getApplicants(postId,user.getUserId());
        return ApiResponse.success(SuccessCode.OK, response);
    }

    @Operation(summary = "지원 거절")
    @PostMapping("/{postId}/applicants/{applicantId}/reject")
    public ApiResponse<Void> rejectApplication(
            @PathVariable("postId") Long postId,
            @PathVariable("applicantId") Long applicantId,
            @AuthenticationPrincipal UserJwtDto user,
            @RequestBody RejectRequest request){
        applicationService.reject(postId,applicantId, request, user.getUserId());
        return ApiResponse.success(SuccessCode.OK);
    }

    @Operation(summary = "지원 수락")
    @PostMapping("/{postId}/applications/{applicationId}/accept")
    public ApiResponse<Void> acceptApplication(
            @PathVariable Long postId,
            @PathVariable Long applicationId,
            @AuthenticationPrincipal UserJwtDto user,
            @RequestBody AcceptRequest request
    ) {
        applicationService.accept(postId, applicationId, user.getUserId(), request);
        return ApiResponse.success(SuccessCode.OK);
    }


}
