package com.paassible.meetservice.client.board;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "board-service", url = "${board-service.url}")
public interface BoardClient {

    @GetMapping("/board/internal/{boardId}/exists")
    void validateBoard(@PathVariable Long boardId);

    @GetMapping("/board/internal/{boardId}/user/{userId}/exists")
    void validateUserInBoard(@PathVariable Long boardId, @PathVariable Long userId);

}