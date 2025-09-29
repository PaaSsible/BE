package com.paassible.recruitservice.post.dto;

import com.paassible.recruitservice.post.entity.MainCategory;
import com.paassible.recruitservice.post.entity.ProjectDuration;
import com.paassible.recruitservice.post.entity.SubCategory;

import java.time.LocalDate;
import java.util.List;

public record PostDetailResponse (
        MainCategory mainCategory,
        SubCategory subCategory,
        Long postId,
        String title,
        String content,
        LocalDate deadline,
        ProjectDuration projectDuration,
        Long writerId,
        String writerName,
        List<RecruitInfo> recruits
){
}
