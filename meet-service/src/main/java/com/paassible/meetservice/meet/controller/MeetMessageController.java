package com.paassible.meetservice.meet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MeetMessageController {

    @MessageMapping("/meet/{meetId}/join")
    @SendTo("/topic/meet/{meetId}/participants")
    public String handleJoin(@Payload String message){
        System.out.println("[JOIN]"+ message);
        return message+" 님이 회의에 입장했습니다.";

    }
}
