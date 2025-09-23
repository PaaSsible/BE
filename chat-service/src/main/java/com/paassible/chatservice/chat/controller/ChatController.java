package com.paassible.chatservice.chat.controller;

import com.paassible.chatservice.chat.dto.ChatMessageRequest;
import com.paassible.chatservice.chat.dto.ChatMessageResponse;
import com.paassible.chatservice.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chats/{roomId}/send")
    public void sendMessage(
            @DestinationVariable Long roomId,
            ChatMessageRequest request) {
        ChatMessageResponse response = chatMessageService.saveMessage(roomId, request);
        messagingTemplate.convertAndSend("/topic/chats/" + roomId, response);
    }
}