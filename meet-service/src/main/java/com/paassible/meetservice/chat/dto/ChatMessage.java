package com.paassible.meetservice.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ChatMessage (
        Long id,
        Long meetId,
        Long senderId,
        String senderName,
        Long targetUserId,
        String content,
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS",
                timezone = "Asia/Seoul")
        LocalDateTime timestamp
){}
