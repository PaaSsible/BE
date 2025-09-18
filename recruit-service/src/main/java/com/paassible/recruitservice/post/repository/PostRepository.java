package com.paassible.recruitservice.post.repository;

import com.paassible.recruitservice.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    Page<Post> findByWriterId(Long writerId, Pageable pageable);


}

