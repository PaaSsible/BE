package com.paassible.meetservice.chat.dto;

import java.time.LocalDateTime;

public record ChatMessage (
        Long meetId,
        Long senderId,
        String senderName,
        Long targetUserId,
        String content,
        LocalDateTime timestamp
){}
