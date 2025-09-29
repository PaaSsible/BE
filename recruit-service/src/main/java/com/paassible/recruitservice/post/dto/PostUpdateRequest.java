package com.paassible.recruitservice.post.dto;

import com.paassible.recruitservice.post.entity.MainCategory;
import com.paassible.recruitservice.post.entity.ProjectDuration;
import com.paassible.recruitservice.post.entity.SubCategory;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record PostUpdateRequest(
        MainCategory mainCategory,
        SubCategory subCategory,
        @NotBlank String title,
        @NotBlank String content,
        @NotNull @FutureOrPresent LocalDate deadline,
        @NotNull ProjectDuration projectDuration,
        @NotNull List<RecruitInfo> recruits
        ) {
}
