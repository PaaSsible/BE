package com.paassible.chatservice.chat.controller;

import com.paassible.chatservice.chat.dto.ChatMessageResponse;
import com.paassible.chatservice.chat.dto.UploadResponse;
import com.paassible.chatservice.chat.dto.MessageReadDetailResponse;
import com.paassible.chatservice.chat.entity.enums.MessageType;
import com.paassible.chatservice.chat.service.ChatRoomMessageService;
import com.paassible.common.dto.CursorPageResponse;
import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatMessageController {

    private final ChatRoomMessageService chatMessageService;

    @GetMapping("/rooms/{roomId}/messages")
    @Operation(summary = "채팅방 상세 조회", description = "채팅방의 메시지 목록을 조회한다.")
    public ResponseEntity<ApiResponse<CursorPageResponse<ChatMessageResponse>>> getMessages(
            @AuthenticationPrincipal UserJwtDto user,
            @PathVariable Long roomId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "5") int size
    ) {
        CursorPageResponse<ChatMessageResponse> response = chatMessageService.getMessages(user.getUserId(), roomId, cursor, size);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }

    @GetMapping("/rooms/{roomId}/messages/{messageId}/reads")
    @Operation(summary = "채팅방 메시지 읽음 상세 조회", description = "채팅방의 메시지를 읽은 멤버와 읽지 않은 멤버를 조회합니다.")
    public ResponseEntity<ApiResponse<MessageReadDetailResponse>> getMessageReadDetail(@AuthenticationPrincipal UserJwtDto user,
                                                                                       @PathVariable Long roomId,
                                                                                       @PathVariable Long messageId) {
        MessageReadDetailResponse response = chatMessageService.getMessageReadDetail(user.getUserId(), roomId, messageId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }

    @PostMapping(value = "/files/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "채팅방 파일/이미지 업로드", description = "채팅방에 이미지 또는 파일을 업로드합니다.")
    public ResponseEntity<ApiResponse<UploadResponse>> upload(
            @RequestParam("type") MessageType type,
            @RequestPart("file") MultipartFile file) {
        UploadResponse response = chatMessageService.saveFile(type, file);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }
}
