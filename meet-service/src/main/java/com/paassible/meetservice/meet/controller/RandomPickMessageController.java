package com.paassible.meetservice.meet.controller;

import com.paassible.meetservice.meet.service.RandomSpeakerService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class RandomPickMessageController {

    private final RandomSpeakerService randomSpeakerService;

    @MessageMapping("/meet/{meetId}/random-pick")
    public void pickRandomSpeaker(
            @DestinationVariable Long meetId,
            Principal principal
    ) {
        Long requesterId = Long.valueOf(principal.getName());
        randomSpeakerService.pickAndBroadcast(meetId, requesterId);
    }
}
