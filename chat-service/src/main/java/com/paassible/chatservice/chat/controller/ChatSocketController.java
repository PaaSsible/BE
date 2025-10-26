package com.paassible.chatservice.chat.controller;

import com.paassible.chatservice.chat.dto.ChatMessageRequest;
import com.paassible.chatservice.chat.dto.ChatMessageResponse;
import com.paassible.chatservice.chat.dto.MessageReadResponse;
import com.paassible.chatservice.chat.entity.enums.ReadType;
import com.paassible.chatservice.chat.service.ChatRoomMessageService;
import com.paassible.chatservice.chat.service.MessageReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatSocketController {

    private final ChatRoomMessageService chatMessageService;
    private final MessageReadService messageReadService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chats/rooms/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            ChatMessageRequest request,
            Principal principal) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) principal;
        Long userId = (Long) authentication.getPrincipal();

        ChatMessageResponse response = chatMessageService.saveMessage(roomId, userId, request);
        messagingTemplate.convertAndSend("/topic/chats/rooms/" + roomId, response);
    }

    @MessageMapping("/chats/rooms/{roomId}/read")
    public void readMessage(@DestinationVariable Long roomId,
                            MessageReadResponse request,
                            Principal principal) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) principal;
        Long userId = (Long) authentication.getPrincipal();

        messageReadService.markAsRead(userId, roomId, request.getMessageId(), ReadType.MESSAGE_READ);
    }
}