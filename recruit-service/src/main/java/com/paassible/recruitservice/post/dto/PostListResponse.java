package com.paassible.recruitservice.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS",
                timezone = "Asia/Seoul")

        LocalDateTime createdAt,
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS",
                timezone = "Asia/Seoul")
        LocalDateTime modifiedAt,
        LocalDate deadline,
        int viewCount,
        int applicationCount,
        List<RecruitInfo> recruits
) {}
