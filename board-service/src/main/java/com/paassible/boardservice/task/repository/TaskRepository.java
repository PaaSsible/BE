package com.paassible.boardservice.task.repository;

import com.paassible.boardservice.task.entity.Task;
import com.paassible.boardservice.task.entity.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByBoardId(Long boardId);

    @Query("SELECT t.status AS status, COUNT(t) AS count " +
            "FROM Task t " +
            "WHERE t.boardId = :boardId " +
            "GROUP BY t.status")
    List<TaskCountProjection> countTasksByStatus(@Param("boardId") Long boardId);

    long countByBoardIdAndDueDateBetween(Long boardId, LocalDate start, LocalDate end);

    // 이번 주 마감 포함 완료된 업무 수
    long countByBoardIdAndDueDateBetweenAndStatus(Long boardId, LocalDate start, LocalDate end, TaskStatus status);

}
