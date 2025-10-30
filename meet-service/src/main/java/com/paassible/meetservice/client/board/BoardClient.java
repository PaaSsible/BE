package com.paassible.meetservice.client.board;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "board-service", url = "${board-service.url}")
public interface BoardClient {

    @GetMapping("/boards/internal/{boardId}/exists")
    void validateBoard(@PathVariable Long boardId);

    @GetMapping("/boards/internal/{boardId}/user/{userId}/exists")
    void validateUserInBoard(@PathVariable Long boardId, @PathVariable Long userId);

    @GetMapping("/boards/internal/{boardId}/members")
    List<BoardMemberResponse> getBoardMembers(@PathVariable Long boardId);

    @GetMapping("/boards/internal/{boardId}/name")
    String getBoardName(@PathVariable("boardId") Long boardId);
}