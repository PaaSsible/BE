package com.paassible.chatservice.chat.controller;

import com.paassible.chatservice.chat.service.ChatRoomService;
import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
