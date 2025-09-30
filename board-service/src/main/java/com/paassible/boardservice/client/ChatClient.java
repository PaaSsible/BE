package com.paassible.boardservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "chat-service", url = "${chat-service.url}")
public interface ChatClient {

    @PostMapping("/chats/internal/board")
    void createBoardChatRoom(@RequestParam("userId") Long userId,
                             @RequestParam("boardId") Long boardId);

    @PostMapping("/chats/internal/participant")
    void addParticipant(@RequestParam("userId") Long userId,
                        @RequestParam("boardId") Long boardId);
}