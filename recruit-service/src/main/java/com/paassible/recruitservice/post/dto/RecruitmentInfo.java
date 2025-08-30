package com.paassible.recruitservice.post.dto;

import java.util.List;

public record RecruitmentInfo(
        Long position,
        List<Long> stacks
) {
}
