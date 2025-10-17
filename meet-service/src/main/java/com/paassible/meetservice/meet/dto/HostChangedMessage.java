package com.paassible.meetservice.meet.dto;

public record HostChangedMessage (Long meetId, Long newHostId, String newHostName) {}
