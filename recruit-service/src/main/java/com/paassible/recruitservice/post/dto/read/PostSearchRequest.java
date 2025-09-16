package com.paassible.recruitservice.post.dto.read;

import com.paassible.recruitservice.post.entity.MainCategory;
import com.paassible.recruitservice.post.entity.SubCategory;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record PostSearchRequest(
        @Min(0) Integer page,
        @Min(1) @Max(100) Integer size,
        String sort,
        MainCategory mainCategory,
        SubCategory subCategory,
        @Nullable Integer position,
        String keyword
){
    public PostSearchRequest{
        page = (page != null) ? page : 0;
        size = (size != null) ? size : 10;
        sort = (sort != null) ? sort : "RECENT";
    }
}
