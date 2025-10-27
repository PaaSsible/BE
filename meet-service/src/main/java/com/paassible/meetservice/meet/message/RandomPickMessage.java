package com.paassible.meetservice.meet.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RandomPickMessage {
    private Long userId;
    private String pickedAt;
}
