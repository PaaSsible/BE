package com.paassible.meetservice.meet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public record LeaveResponse (
        String status,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<CandidateInfo> candidates
){
    public record CandidateInfo (
            Long userId,
            String nickname
    ){}

    public static LeaveResponse transferRequired(List<CandidateInfo> candidates) {
        return new LeaveResponse("TRANSFER_REQUIRED", candidates);
    }

    public static LeaveResponse left(){
        return new LeaveResponse("LEFT", null);
    }
    public static LeaveResponse ended(){
        return new LeaveResponse("ENDED", null);
    }
}