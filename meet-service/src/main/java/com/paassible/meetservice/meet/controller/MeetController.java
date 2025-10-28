package com.paassible.meetservice.meet.controller;

import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import com.paassible.meetservice.meet.dto.*;
import com.paassible.meetservice.meet.service.MeetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    @Operation(summary = "회의  참가", description = "회의 참가를 요쳥합니다.")
    public ResponseEntity<ApiResponse<MeetJoinResponse>> joinMeet(
            @AuthenticationPrincipal UserJwtDto user,
            @PathVariable Long meetId){
        MeetJoinResponse response = meetService.joinMeet(meetId,user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.CREATED, response));
    }

    @DeleteMapping("/{meetId}/participants")
    @Operation(summary = "회의 나가기", description = "회의 나가기를 요청합니다.")
    public ResponseEntity<ApiResponse<LeaveResponse>> leaveMeet(
            @AuthenticationPrincipal UserJwtDto user,
            @PathVariable Long meetId){
        LeaveResponse response = meetService.leaveMeet(meetId,user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }

    @GetMapping("/boards/{boardId}")
    @Operation(summary = "진행 중인 회의 조회", description = "보드에 진행 중인 회의가 있다면 반환합니다.")
    public ResponseEntity<ApiResponse<MeetOngoingResponse>> getOnGoingMeetByBoard(
            @PathVariable Long boardId){
        MeetOngoingResponse response = meetService.getOngoingMeetByBoard(boardId);

        if(response == null){
            return ResponseEntity.ok(ApiResponse.success(SuccessCode.ONGOING_NOT_FOUND, null));
        }
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));

    }

    @PostMapping("/{meetId}/transfer-and-leave")
    @Operation(summary = "회의 호스트 위임 후 퇴장", description = "현재 호스트가 다른 참가자에게 호스트 권한을 넘기고 회의를 나갑니다.")
    public ResponseEntity<ApiResponse<Void>> transferAadLeave(
            @AuthenticationPrincipal UserJwtDto user,
            @PathVariable Long meetId,
            @RequestBody @Valid HostTransferRequest request){
        meetService.transferAndLeave(meetId, user.getUserId(), request.newHostId());
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.HOST_TRANSFERRED));
    }

}
