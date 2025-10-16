package com.paassible.chatservice.chat.dto;

import com.paassible.chatservice.chat.entity.enums.MessageType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UploadResponse {
    private MessageType type;
    private String url;

    public static UploadResponse from(MessageType type, String url) {
        return UploadResponse.builder()
                .type(type)
                .url(url)
                .build();
    }
}
