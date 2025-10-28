package com.paassible.boardservice.chat.controller;

import com.paassible.boardservice.chat.dto.ChatRoomIdResponse;
import com.paassible.boardservice.chat.dto.ChatRoomInviteRequest;
import com.paassible.boardservice.chat.dto.ChatRoomRequest;
import com.paassible.boardservice.chat.dto.ChatRoomResponse;
import com.paassible.boardservice.client.ChatClient;
import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards/{boardId}/chats")
@RequiredArgsConstructor
@Tag(name = "보드 내 채팅 API", description = "채팅방 생성, 초대, 조회, 퇴장")
public class ChatRoomController {

    private final ChatClient chatClient;

    @PostMapping("/rooms")
    @Operation(summary = "보드 내 채팅방 생성", description = "보드 안에서 새로운 채팅방을 생성한다.")
    public ResponseEntity<ApiResponse<ChatRoomIdResponse>> createChatRoom(
            @AuthenticationPrincipal UserJwtDto user,
            @PathVariable Long boardId,
            @RequestBody ChatRoomRequest request) {

        ChatRoomIdResponse response = chatClient.createChatRoom(user.getUserId(), boardId,  request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.CREATED, response));
    }

    @PostMapping("/rooms/{roomId}/invite")
    @Operation(summary = "서브 채팅방 초대", description = "보드 내 서브 채팅방에 팀원을 초대한다.")
    public ResponseEntity<ApiResponse<Void>> inviteMembersToSubRoom(
            @AuthenticationPrincipal UserJwtDto user,
            @PathVariable Long boardId,
            @PathVariable Long roomId,
            @RequestBody ChatRoomInviteRequest request) {

        // service 따로 만들어서 해당 사람들이 board에 있는 사람들인지 확인
        chatClient.inviteParticipants(user.getUserId(), boardId, roomId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK));
    }

    @GetMapping("/rooms")
    @Operation(summary = "보드 내 채팅방 조회", description = "보드 안의 모든 채팅방을 조회한다.")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getBoardRooms(
            @AuthenticationPrincipal UserJwtDto user,
            @PathVariable Long boardId) {
        List<ChatRoomResponse> response =
                chatClient.getChatRooms(user.getUserId(), boardId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }

    @DeleteMapping("/rooms/{roomId}")
    @Operation(summary = "채팅방 퇴장", description = "유저가 참여중인 채팅방에서 퇴장한다.")
    public ResponseEntity<ApiResponse<Void>> leaveRoom(
            @AuthenticationPrincipal UserJwtDto user,
            @PathVariable Long boardId,
            @PathVariable Long roomId) {
        chatClient.leaveRoom(user.getUserId(), boardId, roomId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.LEAVED));
    }
}
