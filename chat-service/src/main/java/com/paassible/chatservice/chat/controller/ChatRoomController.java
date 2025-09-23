package com.paassible.chatservice.chat.controller;

import com.paassible.chatservice.chat.dto.ChatMessageResponse;
import com.paassible.chatservice.chat.dto.ChatRoomResponse;
import com.paassible.chatservice.chat.dto.DirectChatRequest;
import com.paassible.chatservice.chat.dto.SubChatRequest;
import com.paassible.chatservice.chat.service.ChatMessageService;
import com.paassible.chatservice.chat.service.ChatRoomService;
import com.paassible.common.dto.CursorPageResponse;
import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @GetMapping("/group/{boardId}")
    @Operation(summary = "단체 채팅방 조회", description = "단체 채팅방에 부여된 roomId를 조회합니다.")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> getBoardChatRoom(@PathVariable Long boardId) {
        ChatRoomResponse response = chatRoomService.getGroupChatRoom(boardId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }

    @PostMapping("/direct")
    @Operation(summary = "1:1 채팅방 조회", description = "선택한 상대방과의 1:1 채팅방을 생성하거나 roomId를 조회합니다.")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> getOrCreateDirectRoom(@RequestBody DirectChatRequest request) {
        ChatRoomResponse response = chatRoomService.getOrCreateDirectChat(request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }

    @PostMapping("/group/{boardId}/sub")
    @Operation(summary = "서브 채팅방 생성", description = "새로운 채팅방을 생성합니다.")
    public ResponseEntity<ApiResponse<Void>> createSubChat(@PathVariable Long boardId,
                                                           @RequestBody SubChatRequest request) {
        chatRoomService.createSubChat(boardId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.CREATED));
    }

    @GetMapping("/{roomId}/messages")
    @Operation(summary = "채팅방 메시지 목록 조회", description = "채팅방의 메시지 내용을 조회합니다.")
    public ResponseEntity<ApiResponse<CursorPageResponse<ChatMessageResponse>>> getMessages(
            @PathVariable Long roomId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "5") int size
    ) {
        CursorPageResponse<ChatMessageResponse> response = chatMessageService.getMessages(roomId, cursor, size);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }
}