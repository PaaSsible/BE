package com.paassible.boardservice.client;

import com.paassible.boardservice.portfolio.dto.PortfolioAiRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserClient {

    @GetMapping("/users/internal/{userId}")
    UserResponse getUser(@PathVariable("userId") Long userId);

    @GetMapping("/users/internal/portfolio")
    void generatePortfolioAi(@RequestBody PortfolioAiRequest request);
}