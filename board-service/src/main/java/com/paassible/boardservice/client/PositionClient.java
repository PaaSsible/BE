package com.paassible.boardservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "recruit-service", url = "${recruit-service.url}")
public interface PositionClient {

    @PostMapping("/recruits/internal/position")
    String getPositionName(@RequestParam("positionId") Long positionId);
}