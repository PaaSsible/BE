package com.paassible.meetservice.meet.message;

public record HostChangedMessage (
        Long meetId,
        Long newHostId,
        String newHostNickname
){}
