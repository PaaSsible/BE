package com.paassible.recruitservice.post.dto;

import java.util.List;

public record PagedPostListResponse(
        List<PostListResponse> posts,
        PageInfo pageInfo
) {
    public record PageInfo(
            int currentPage,
            int totalPages,
            long totalElements,
            int size,
            boolean hasNext
    ){}
}
