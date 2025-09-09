package com.paassible.boardservice.task.dto;

import com.paassible.boardservice.task.entity.enums.TaskStatus;
import lombok.Getter;

@Getter
public class TaskStatusRequest {
    private TaskStatus status;
}
