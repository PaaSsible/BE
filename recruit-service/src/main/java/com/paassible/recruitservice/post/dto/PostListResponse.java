package com.paassible.recruitservice.post.dto;

import com.paassible.recruitservice.post.entity.MainCategory;
import com.paassible.recruitservice.post.entity.SubCategory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PostListResponse(
        Long postId,
        String title,
        MainCategory mainCategory,
        SubCategory subCategory,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        LocalDate deadline,
        int viewCount,
        int applicationCount,
        List<RecruitInfo> recruits
) {}
