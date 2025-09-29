package com.paassible.recruitservice.post.repository;

import com.paassible.recruitservice.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {


    Page<Post> findByWriterIdAndClosedFalse(Long writerId, Pageable pageable);


    List<Post> findByDeadlineBeforeAndClosed(LocalDate now, boolean closed);
}


