package com.paassible.chatservice.chat.controller;

import com.paassible.chatservice.chat.dto.MessageReadDetailResponse;
import com.paassible.chatservice.chat.dto.MessageReadRequest;
import com.paassible.chatservice.chat.entity.enums.ReadType;
import com.paassible.chatservice.chat.service.MessageReadService;
import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class MessageReadController {

    private final MessageReadService messageReadService;

    @PatchMapping("/rooms/{roomId}/read-all")
    @Operation(summary = "메시지 일괄 읽음 처리", description = "특정 채팅방에 읽지 않은 메시지를 갱신한다.")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal UserJwtDto user,
            @PathVariable Long roomId,
            @RequestBody MessageReadRequest request) {
        messageReadService.markAsRead(user.getUserId(), roomId, request.getMessageId(), ReadType.MESSAGE_READ_ALL);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.READ));
    }

    @GetMapping("/rooms/{roomId}/messages/{messageId}/reads")
    @Operation(summary = "채팅방 메시지 읽음 상세 조회", description = "채팅방의 메시지를 읽은 멤버를 조회합니다.")
    public ResponseEntity<ApiResponse<MessageReadDetailResponse>> getMessageReadDetail(@AuthenticationPrincipal UserJwtDto user,
                                                                                       @PathVariable Long roomId,
                                                                                       @PathVariable Long messageId) {
        MessageReadDetailResponse response = messageReadService.getMessageReadDetail(user.getUserId(), roomId, messageId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }
}
