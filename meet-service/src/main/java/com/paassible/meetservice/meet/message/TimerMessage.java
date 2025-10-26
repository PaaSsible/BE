package com.paassible.meetservice.meet.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimerMessage {
    private TimerType type;
    private int duration;
    private String serverStartTime;
}