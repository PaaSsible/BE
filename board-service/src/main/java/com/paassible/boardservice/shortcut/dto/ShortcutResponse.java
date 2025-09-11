package com.paassible.boardservice.shortcut.dto;

import com.paassible.boardservice.shortcut.entity.Shortcut;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShortcutResponse {
    private Long id;
    private Long boardId;
    private String name;
    private String url;

    public static ShortcutResponse from(Shortcut shortcut) {
        return ShortcutResponse.builder()
                .id(shortcut.getId())
                .boardId(shortcut.getBoardId())
                .name(shortcut.getName())
                .url(shortcut.getUrl())
                .build();
    }
}
