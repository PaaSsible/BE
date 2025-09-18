package com.paassible.recruitservice.post.repository;

import com.paassible.recruitservice.post.dto.PostListResponse;
import com.paassible.recruitservice.post.dto.PostSearchRequest;
import com.querydsl.core.types.OrderSpecifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<PostListResponse> searchPosts(PostSearchRequest request, Pageable pageable, OrderSpecifier<?> orderSpecifier);

}


