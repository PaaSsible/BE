package com.paassible.meetservice.meet.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingMessage {
    private Long userId;
    private boolean speaking;
}
