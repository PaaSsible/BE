package com.paassible.boardservice.task.dto;

import com.paassible.boardservice.task.entity.Task;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private String status;
    private Long boardId;
    private List<AssigneeDto> assignees;
    private List<String> positions;

    @Getter
    @Builder
    public static class AssigneeDto {
        private Long userId;
        private String name;
    }

    public static TaskResponse from(Task task, List<String> positions, List<AssigneeDto> assignees) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .status(task.getStatus().name())
                .boardId(task.getBoardId())
                .assignees(assignees)
                .positions(positions)
                .build();
    }
}


