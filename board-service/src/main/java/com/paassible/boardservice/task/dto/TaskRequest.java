package com.paassible.boardservice.task.dto;


import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TaskRequest {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Long assigneeId;
    private Long boardId;
}
