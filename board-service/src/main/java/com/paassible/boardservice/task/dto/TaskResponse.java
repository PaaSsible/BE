package com.paassible.boardservice.task.dto;

import com.paassible.boardservice.task.entity.Task;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private LocalDateTime dueDate;
    private String status;
    private Long assigneeId;
    private Long boardId;

    public static TaskResponse from(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .dueDate(task.getDueDate())
                .status(task.getStatus().name())
                .assigneeId(task.getAssigneeId())
                .boardId(task.getBoardId())
                .build();
    }
}


