package com.paassible.boardservice.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContributionResponse {
    private Long id;
    private String memberName;
    private String part;
    private double taskCompletion;
    private double attendanceRate;
    private CommunicationFrequency communicationFrequency;
    private double contribution;

    @Getter
    @AllArgsConstructor
    public static class CommunicationFrequency {
        private int value;
        private int total;
    }
}