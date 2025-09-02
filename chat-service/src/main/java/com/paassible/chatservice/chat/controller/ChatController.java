package com.paassible.chatservice.chat.controller;

import com.paassible.chatservice.chat.dto.ChatMessageRequest;
import com.paassible.chatservice.chat.dto.ChatMessageResponse;
import com.paassible.chatservice.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat.send.{roomId}")
    @SendTo("/topic/chatroom.{roomId}")
    public ChatMessageResponse sendMessage(
            @DestinationVariable Long roomId,
            ChatMessageRequest request) {
        ChatMessageResponse response = chatMessageService.saveMessage(roomId, request);
        return response;
    }
}