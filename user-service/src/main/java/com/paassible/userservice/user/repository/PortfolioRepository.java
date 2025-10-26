package com.paassible.userservice.user.repository;

import com.paassible.userservice.user.entity.Portfolio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    Page<Portfolio> findByUserId(Long userId, Pageable pageable);
}
