package com.paassible.boardservice.task.dto;


import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class TaskRequest {
    private String title;
    private LocalDateTime dueDate;
    private List<Long> assigneeIds;
    private List<Long> positionIds;
}
