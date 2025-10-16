package com.paassible.chatservice.chat.controller;

import com.paassible.chatservice.chat.dto.ChatMessageRequest;
import com.paassible.chatservice.chat.dto.ChatMessageResponse;
import com.paassible.chatservice.chat.dto.MessageReadRequest;
import com.paassible.chatservice.chat.dto.MessageReadResponse;
import com.paassible.chatservice.chat.service.ChatRoomMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chats/rooms/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            ChatMessageRequest request,
            Principal principal) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) principal;
        Long userId = (Long) authentication.getPrincipal();

        ChatMessageResponse response = chatMessageService.saveMessage(roomId, userId, request);
        messagingTemplate.convertAndSend("/topic/chats/rooms" + roomId, response);
    }
}