package com.paassible.meetservice.meet.controller;

import com.paassible.meetservice.meet.service.TimerService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class TimerMessageController {

    private final TimerService timerService;

    @MessageMapping("/meet/{meetId}/timer/start")
    public void start(@DestinationVariable Long meetId, @Payload Integer duration, Principal principal) {
        timerService.startTimer(meetId, Long.valueOf(principal.getName()), duration);
    }

    @MessageMapping("/meet/{meetId}/timer/pause")
    public void pause(@DestinationVariable Long meetId, Principal principal) {
        timerService.pauseTimer(meetId, Long.valueOf(principal.getName()));
    }

    @MessageMapping("/meet/{meetId}/timer/resume")
    public void resume(@DestinationVariable Long meetId, Principal principal) {
        timerService.resumeTimer(meetId, Long.valueOf(principal.getName()));
    }

    @MessageMapping("/meet/{meetId}/timer/end")
    public void end(@DestinationVariable Long meetId, Principal principal) {
        timerService.endTimer(meetId, Long.valueOf(principal.getName()));
    }
}
