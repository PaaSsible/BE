package com.paassible.recruitservice.position.repositoty;

import com.paassible.recruitservice.position.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {}
