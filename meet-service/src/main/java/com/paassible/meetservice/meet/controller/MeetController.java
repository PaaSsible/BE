package com.paassible.meetservice.meet.controller;

import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import com.paassible.meetservice.meet.dto.MeetCreateRequest;
import com.paassible.meetservice.meet.dto.MeetCreateResponse;
import com.paassible.meetservice.meet.dto.MeetJoinResponse;
import com.paassible.meetservice.meet.service.MeetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meets")
@RequiredArgsConstructor
@Tag(name = "회의 API", description = "화상 회의 API")
public class MeetController {

    private final MeetService meetService;

    @PostMapping
    @Operation(summary = "회의 생성", description = "새로운 회의를 생성합니다.")
    public ResponseEntity<ApiResponse<MeetCreateResponse>> createMeet(
            @AuthenticationPrincipal UserJwtDto user,
            @RequestBody MeetCreateRequest meetCreateRequest) {
        MeetCreateResponse response = meetService.createMeet(user.getUserId(), meetCreateRequest);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.CREATED, response));
    }

    @PostMapping("/{meetId}/participants")
    @Operation(summary = "회의 참가", description = "회의 참가를 요쳥합니다.")
    public ResponseEntity<ApiResponse<MeetJoinResponse>> joinMeet(
            @AuthenticationPrincipal UserJwtDto user,
            @PathVariable Long meetId){
        MeetJoinResponse response = meetService.joinMeet(meetId,user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.CREATED, response));
    }

    @DeleteMapping("/{meetId}/participants")
    @Operation(summary = "회의 나가기", description = "회의 나가기를 요청합니다.")
    public ResponseEntity<ApiResponse<Void>> leaveMeet(
            @AuthenticationPrincipal UserJwtDto user,
            @PathVariable Long meetId){
        meetService.leaveMeet(meetId,user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.DELETED));
    }
}
