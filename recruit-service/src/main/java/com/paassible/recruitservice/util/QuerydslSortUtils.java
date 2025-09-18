package com.paassible.recruitservice.util;

import com.paassible.recruitservice.post.entity.QPost;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class QuerydslSortUtils {
    public static OrderSpecifier<?> toOrderSpecifier(String sort, QPost post) {
        if (sort == null) return post.createdAt.desc(); // default: RECENT

        return switch (sort.toUpperCase()) {
            case "DEADLINE" -> post.deadline.asc();
            case "POPULAR" -> post.applicationCount.desc();
            default -> post.createdAt.desc(); // RECENT
        };
    }
}


