package com.paassible.chatservice.chat.controller;

import com.paassible.chatservice.chat.dto.ChatMessageRequest;
import com.paassible.chatservice.chat.dto.ChatMessageResponse;
import com.paassible.chatservice.chat.dto.MessageReadRequest;
import com.paassible.chatservice.chat.dto.MessageReadResponse;
import com.paassible.chatservice.chat.service.ChatMessageService;
import com.paassible.chatservice.chat.service.MessageReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final MessageReadService messageReadService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chats/{roomId}/send")
    public void sendMessage(
            @DestinationVariable Long roomId,
            ChatMessageRequest request,
            Principal principal) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) principal;
        Long userId = (Long) authentication.getPrincipal();

        ChatMessageResponse response = chatMessageService.saveMessage(roomId, userId, request);
        messagingTemplate.convertAndSend("/topic/chats/" + roomId, response);
    }

    @MessageMapping("/room/{roomId}/read")
    public void handleRead(@DestinationVariable Long roomId, MessageReadRequest request, Principal principal) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) principal;
        Long userId = (Long) authentication.getPrincipal();

        MessageReadResponse response = messageReadService.markAsRead(userId, roomId, request.getMessageId());
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/reads", response);
    }
}