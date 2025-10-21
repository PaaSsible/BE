package com.paassible.boardservice.task.dto;


import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class TaskRequest {
    private String title;
    private LocalDate dueDate;
    private List<Long> assigneeIds;
    private List<Long> positionIds;
}
