package com.paassible.chatservice.chat.controller;

import com.paassible.chatservice.chat.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Hidden
@RestController
@RequestMapping("/chats/internal")
@RequiredArgsConstructor
public class ChatInternalController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/board")
    public ResponseEntity<Void> createBoardChatRoom(@RequestParam("userId") Long userId,
                                                    @RequestParam("boardId") Long boardId) {
        chatRoomService.createGroupChat(userId, boardId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/participant")
    public ResponseEntity<Void> addParticipant(@RequestParam("userId") Long userId,
                                               @RequestParam("boardId") Long boardId) {
        chatRoomService.addParticipant(userId, boardId);
        return ResponseEntity.noContent().build();
    }
}
