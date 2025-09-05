package com.paassible.meetservice.meet.repository;

import com.paassible.meetservice.meet.entity.Meet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetRepository extends JpaRepository<Meet, Long> {

}
