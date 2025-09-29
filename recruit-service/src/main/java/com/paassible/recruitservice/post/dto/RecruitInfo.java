package com.paassible.recruitservice.post.dto;

import java.util.List;

public record RecruitInfo(
        Long position,
        List<Long> stacks
) {
}
