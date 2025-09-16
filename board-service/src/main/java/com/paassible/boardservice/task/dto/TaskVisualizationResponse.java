package com.paassible.boardservice.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TaskVisualizationResponse {
    private int completedRate;
    private int total;
    private List<TaskDto> tasks;

    @Getter
    @AllArgsConstructor
    public static class TaskDto {
        private String status;
        private int count;
    }

    public static TaskVisualizationResponse from(int completedRate, int total, List<TaskDto> tasks) {
        return TaskVisualizationResponse.builder()
                .completedRate(completedRate)
                .total(total)
                .tasks(tasks)
                .build();
    }
}
