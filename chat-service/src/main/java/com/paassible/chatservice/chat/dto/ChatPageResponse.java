package com.paassible.chatservice.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class ChatPageResponse {
    private List<ChatMessageResponse> messages;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private boolean hasNext;
    private boolean hasPrevious;
    private int size;

    public static ChatPageResponse from(Page<ChatMessageResponse> page) {
        return ChatPageResponse.builder()
                .messages(page.getContent())
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .size(page.getSize())
                .build();
    }
}