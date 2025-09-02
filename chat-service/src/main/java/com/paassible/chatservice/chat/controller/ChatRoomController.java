package com.paassible.chatservice.chat.controller;

import com.paassible.chatservice.chat.dto.ChatRoomResponse;
import com.paassible.chatservice.chat.dto.JoinDirectRequest;
import com.paassible.chatservice.chat.entity.ChatRoom;
import com.paassible.chatservice.chat.service.ChatRoomService;
import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/group/{boardId}")
    @Operation(summary = "단체 채팅방 roomId 조회", description = "단체 채팅방에 부여된 roomId를 조회합니다.")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> getBoardChatRoom(@PathVariable Long boardId) {
        ChatRoomResponse response = chatRoomService.getGroupChatRoom(boardId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }

    @PostMapping("/direct")
    @Operation(summary = "1:1 채팅방 생성 or roomId 조회", description = "선택한 상대방과의 1:1 채팅방을 생성하거나 roomId를 조회합니다.")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> getOrCreateDirectRoom(@RequestBody JoinDirectRequest request) {
        ChatRoomResponse response = chatRoomService.getOrCreateDirectRoom(request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }


}