package com.paassible.boardservice.task.entity;

import com.paassible.boardservice.task.entity.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private Long assigneeId;

    private Long boardId;

    public void updateStatus(TaskStatus status) {
        this.status = status;
    }

    public void updateTask(String title, LocalDateTime dueDate, Long assigneeId) {
        this.title = title;
        this.dueDate = dueDate;
        this.assigneeId = assigneeId;
    }

    public void updateTaskDescription(String description) {
        this.description = description;
    }
}
