package com.paassible.chatservice.chat.controller;

import com.paassible.chatservice.chat.dto.ChatRoomInviteRequest;
import com.paassible.chatservice.chat.dto.ChatRoomRequest;
import com.paassible.chatservice.chat.dto.ChatRoomResponse;
import com.paassible.chatservice.chat.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Hidden
@RestController
@RequestMapping("/chats/internal")
@RequiredArgsConstructor
public class ChatInternalController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/board")
    public ResponseEntity<Void> createGroupChat(@RequestParam("userId") Long userId,
                                                    @RequestParam("boardId") Long boardId,
                                                    @RequestParam("boardName") String boardName) {
        chatRoomService.createGroupChat(userId, boardId, boardName);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/participant")
    public ResponseEntity<Void> addGroupParticipant(@RequestParam("userId") Long userId,
                                               @RequestParam("boardId") Long boardId) {
        chatRoomService.addGroupParticipant(userId, boardId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/rooms")
    public ResponseEntity<Void> createChatRoom(@RequestParam("userId") Long userId,
                                               @RequestParam("boardId") Long boardId,
                                               @RequestBody ChatRoomRequest request) {
        chatRoomService.createChatRoom(userId, boardId, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/rooms/{roomId}/invite")
    public ResponseEntity<Void> inviteParticipants(@RequestParam("userId") Long userId,
                                                   @RequestParam("boardId") Long boardId,
                                                   @PathVariable Long roomId,
                                                   @RequestBody ChatRoomInviteRequest request) {
        chatRoomService.addSubParticipant(userId, boardId, roomId, request);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getUserChatRooms(@RequestParam("userId") Long userId,
                                                            @RequestParam("boardId") Long boardId) {
        return ResponseEntity.ok(chatRoomService.getChatRooms(userId, boardId));
    }

    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<Void> leaveChatRoom(@RequestParam("userId") Long userId,
                                              @RequestParam("boardId") Long boardId,
                                              @PathVariable("roomId") Long roomId) {
        chatRoomService.leaveRoom(userId, boardId, roomId);
        return ResponseEntity.noContent().build();
    }
}
