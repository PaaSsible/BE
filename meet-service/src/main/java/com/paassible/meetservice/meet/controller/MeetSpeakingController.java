package com.paassible.meetservice.meet.controller;

import com.paassible.meetservice.meet.message.SpeakingMessage;
import com.paassible.meetservice.meet.service.SpeakingTrackerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MeetSpeakingController {

    private final SpeakingTrackerService trackerService;

    @MessageMapping("/meet/{meetId}/speaking")
    public void handleSpeaking(
            @DestinationVariable Long meetId,
            @Payload SpeakingMessage message
    ){
        trackerService.updateSpeaking(meetId, message.getUserId(), message.isSpeaking());
    }
}
