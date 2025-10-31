package com.paassible.recruitservice.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.paassible.recruitservice.post.entity.MainCategory;
import com.paassible.recruitservice.post.entity.ProjectDuration;
import com.paassible.recruitservice.post.entity.SubCategory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PostDetailResponse (
        MainCategory mainCategory,
        SubCategory subCategory,
        Long postId,
        String title,
        String content,
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd",
                timezone = "Asia/Seoul")
        LocalDate deadline,
        ProjectDuration projectDuration,
        Long writerId,
        String writerName,
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS",
                timezone = "Asia/Seoul")
        LocalDateTime createdAt,
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS",
                timezone = "Asia/Seoul")
        LocalDateTime updatedAt,
        int viewCount,
        int applicationCount,
        List<RecruitInfo> recruits
){
}
