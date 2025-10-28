package com.paassible.userservice.user.controller;

import com.paassible.userservice.user.dto.PortfolioAiRequest;
import com.paassible.userservice.user.dto.UserResponse;
import com.paassible.userservice.user.service.PortfolioService;
import com.paassible.userservice.user.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/internal")
public class UserInternalController {

    private final UserService userService;
    private final PortfolioService portfolioService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        UserResponse response = userService.getProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/portfolio")
    public ResponseEntity<Void> createPost(@RequestBody PortfolioAiRequest request) {
        portfolioService.createPortfolioAi(request);
        return ResponseEntity.noContent().build();
    }
}
