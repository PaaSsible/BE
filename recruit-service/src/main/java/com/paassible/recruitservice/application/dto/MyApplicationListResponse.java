package com.paassible.recruitservice.application.dto;

import com.paassible.recruitservice.application.entity.ApplicationStatus;
import com.paassible.recruitservice.post.dto.RecruitInfo;
import com.paassible.recruitservice.post.entity.MainCategory;
import com.paassible.recruitservice.post.entity.SubCategory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record MyApplicationListResponse(
        Long postId,
        String title,
        MainCategory mainCategory,
        SubCategory subCategory,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        LocalDate deadline,
        int viewCount,
        int applicationCount,
        List<RecruitInfo> recruits,
        ApplicationStatus status
) {}
