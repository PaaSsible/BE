package com.paassible.recruitservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;



@FeignClient(name = "board-service", url = "${board-service.url}")
public interface BoardClient {

    @PostMapping("/boards/internal/{boardId}/members")
    void addMember(@PathVariable Long boardId,
                                  @RequestParam Long userId);


    @GetMapping("/boards/internal/{boardId}/user/{userId}/exists")
    void existUserInBoard(@PathVariable Long boardId, @PathVariable Long userId);

}
