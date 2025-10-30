package com.paassible.chatservice.chat.controller;

import com.paassible.chatservice.chat.dto.InviteMemberRequest;
import com.paassible.chatservice.chat.dto.InviteMemberResponse;
import com.paassible.chatservice.chat.service.ChatRoomService;
import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @DeleteMapping("/rooms/{roomId}")
    @Operation(summary = "채팅방 퇴장", description = "유저가 참여중인 채팅방에서 퇴장한다.")
    public ResponseEntity<ApiResponse<Void>> leaveRoom(
            @AuthenticationPrincipal UserJwtDto user,
            @PathVariable Long roomId) {
        chatRoomService.leaveRoom(user.getUserId(), roomId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.LEAVED));
    }

    @PostMapping("/rooms/{roomId}/member")
    @Operation(summary = "채팅방 초대 팀원 목록", description = "유저가 참여중인 채팅방에 초대할 팀원 목록을 조회한다.")
    public ResponseEntity<ApiResponse<List<InviteMemberResponse>>> getInvitableMembers(
            @AuthenticationPrincipal UserJwtDto user,
            @PathVariable Long roomId,
            @RequestBody InviteMemberRequest request) {
        List<InviteMemberResponse> response = chatRoomService.getInvitableMembers(user.getUserId(), request.getBoardMemberIds(), roomId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }

    @GetMapping("/rooms/{roomId}/member")
    @Operation(summary = "현재 채팅방 팀원 목록", description = "유저가 참여중인 채팅방의 팀원 목록을 조회한다.")
    public ResponseEntity<ApiResponse<List<InviteMemberResponse>>> getInvitableMembers(
            @AuthenticationPrincipal UserJwtDto user,
            @PathVariable Long roomId) {
        List<InviteMemberResponse> response = chatRoomService.getRoomParticipants(user.getUserId(), roomId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }
}
