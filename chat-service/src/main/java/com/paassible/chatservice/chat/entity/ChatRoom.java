package com.paassible.chatservice.chat.entity;

import com.paassible.chatservice.chat.entity.enums.RoomType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long boardId;

    private String name;

    @Enumerated(EnumType.STRING)
    private RoomType type;
}