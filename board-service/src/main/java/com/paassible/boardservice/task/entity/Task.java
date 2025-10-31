package com.paassible.boardservice.task.entity;

import com.paassible.boardservice.task.entity.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private Long boardId;

    public void updateStatus(TaskStatus status) {
        this.status = status;
    }

    public void updateTask(String title, LocalDate dueDate) {
        this.title = title;
        this.dueDate = dueDate;
    }

    public void updateTaskDescription(String description) {
        this.description = description;
    }
}
