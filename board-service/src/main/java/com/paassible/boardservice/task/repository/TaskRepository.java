package com.paassible.boardservice.task.repository;

import com.paassible.boardservice.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByBoardId(Long boardId);

    @Query("SELECT t.status AS status, COUNT(t) AS count " +
            "FROM Task t " +
            "GROUP BY t.status")
    List<TaskCountProjection> countTasksByStatus();

}
