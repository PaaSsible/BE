package com.paassible.recruitservice.post.dto;

import com.paassible.recruitservice.post.entity.Post;
import com.paassible.recruitservice.post.entity.ProjectDuration;

import java.time.LocalDate;
import java.util.List;

public record PostDetailResponse (
        Long postId,
        String title,
        String content,
        LocalDate deadline,
        ProjectDuration projectDuration,
        Long writerId,
        List<RecruitmentInfo> recruitment
){
}
