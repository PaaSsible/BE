package com.paassible.meetservice.chat.controller;

import com.paassible.common.security.dto.UserJwtDto;
import com.paassible.meetservice.chat.dto.ChatMessage;
import com.paassible.meetservice.util.ChatKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meets/{meetId}/chats")
public class ChatQueryController {

    private final RedisTemplate<String, ChatMessage> chatRedis;
    private final RedisTemplate<String, String> stringRedis;


    @GetMapping("/public")
    public ResponseEntity<List<ChatMessage>> getPublic(
            @PathVariable Long meetId,
            @AuthenticationPrincipal UserJwtDto user) {

        String key = ChatKeys.publicChat(meetId);
        List<ChatMessage> result = chatRedis.opsForList().range(key, 0, -1);
        return ResponseEntity.ok(result != null ? result : List.of());
    }

    @GetMapping("/dm")
    public ResponseEntity<List<ChatMessage>> getMyDm(
            @PathVariable Long meetId,
            @AuthenticationPrincipal UserJwtDto user) {

        String key = ChatKeys.dmChat(meetId, user.getUserId());
        List<ChatMessage> result = chatRedis.opsForList().range(key, 0, -1);
        return ResponseEntity.ok(result != null ? result : List.of());
    }
}
