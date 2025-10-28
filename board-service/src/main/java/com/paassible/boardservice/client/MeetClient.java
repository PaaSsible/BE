package com.paassible.boardservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "meet-service", url = "${meet-service.url}")
public interface MeetClient {

    @GetMapping("/meets/internal/attendance/rate")
     Double getContribution (@RequestParam("userId") Long userId,
                         @RequestParam("boardId") Long boardId);

}