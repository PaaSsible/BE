package com.paassible.meetservice.meet.repository;

import com.paassible.meetservice.meet.entity.Meet;
import com.paassible.meetservice.meet.entity.MeetingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MeetRepository extends JpaRepository<Meet, Long> {

    boolean existsByBoardIdAndStatus(Long boardId, MeetingStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM Meet m WHERE m.id = :id")
    Optional<Meet> findByIdWithLock(@Param("id") Long id);

    Optional<Meet> findByBoardIdAndStatus(Long boardId, MeetingStatus status);

}
