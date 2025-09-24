package com.paassible.chatservice.chat.controller;

import com.paassible.chatservice.chat.dto.MessageReadDetailResponse;
import com.paassible.chatservice.chat.service.MessageReadService;
import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats/messages")
public class MessageReadController {

    private final MessageReadService messageReadService;

    @GetMapping("/{messageId}/reads")
    @Operation(summary = "채팅방 메시지 읽음 상세 조회", description = "채팅방의 메시지를 읽은 멤버와 읽지 않은 멤버를 조회합니다.")
    public ResponseEntity<ApiResponse<MessageReadDetailResponse>> getMessageReadDetail(@PathVariable Long messageId) {
        MessageReadDetailResponse response = messageReadService.getMessageReadDetail(messageId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }
}
