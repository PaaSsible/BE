package com.paassible.recruitservice.post.dto;

import com.paassible.recruitservice.post.entity.ProjectDuration;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record PostCreateRequest(
        @NotBlank String mainCategory,
        @NotBlank String subCategory,
        @NotBlank String title,
        @NotBlank String content,
        @FutureOrPresent  LocalDate deadline,
        @NotNull ProjectDuration projectDuration,
        List<RecruitmentInfo> recruitment) {

}
