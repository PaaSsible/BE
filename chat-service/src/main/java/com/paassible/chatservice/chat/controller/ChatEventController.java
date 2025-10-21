package com.paassible.chatservice.chat.controller;

import com.paassible.chatservice.chat.dto.MessageReadRequest;
import com.paassible.chatservice.chat.service.ChatRoomMessageService;
import com.paassible.chatservice.chat.service.ChatRoomService;
import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatEventController {

    private final ChatRoomMessageService chatRoomMessageService;

    @PatchMapping("/rooms/{roomId}/read")
    @Operation(summary = "메시지 읽음 처리", description = "특정 채팅방에서 사용자의 마지막 읽은 메시지를 갱신한다.")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal UserJwtDto user,
            @PathVariable Long roomId,
            @RequestBody MessageReadRequest request) {
        chatRoomMessageService.markAsRead(user.getUserId(), roomId, request.getLastMessageId());
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.READ));
    }
}
