package com.paassible.recruitservice.post.repository;


import com.paassible.recruitservice.post.dto.read.PostListResponse;
import com.paassible.recruitservice.post.dto.read.PostSearchRequest;
import com.paassible.recruitservice.post.entity.Post;
import com.paassible.recruitservice.post.entity.QPost;
import com.paassible.recruitservice.post.entity.QRecruitment;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostListResponse> searchPosts(PostSearchRequest request,
                                              Pageable pageable,
                                              OrderSpecifier<?> orderSpecifier) {
        QPost post = QPost.post;
        QRecruitment recruitment = QRecruitment.recruitment;

        BooleanBuilder builder = new BooleanBuilder();

        if (request.mainCategory() != null) {
            builder.and(post.mainCategory.eq(request.mainCategory()));
        }
        if (request.subCategory() != null) {
            builder.and(post.subCategory.eq(request.subCategory()));
        }

        if (request.keyword() != null && !request.keyword().trim().isEmpty()) {
            builder.and(post.title.containsIgnoreCase(request.keyword()));
        }

        List<Post> posts;
        Long total;

        if (request.position() != null) {
            posts = queryFactory
                    .selectFrom(post)
                    .join(recruitment).on(post.id.eq(recruitment.postId))
                    .where(builder.and(recruitment.positionId.eq(request.position().longValue())))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .orderBy(orderSpecifier)
                    .fetch();

            total = queryFactory
                    .select(post.countDistinct()) // join 시 중복 제거
                    .from(post)
                    .join(recruitment).on(post.id.eq(recruitment.postId))
                    .where(builder.and(recruitment.positionId.eq(request.position().longValue())))
                    .fetchOne();
        }

        else {
            posts = queryFactory
                    .selectFrom(post)
                    .where(builder)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .orderBy(orderSpecifier)
                    .fetch();

            total = queryFactory
                    .select(post.count())
                    .from(post)
                    .where(builder)
                    .fetchOne();
        }

        if (posts.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, total != null ? total : 0);
        }

        List<Long> postIds = posts.stream().map(Post::getId).toList();

        List<Tuple> recruitmentTuples = queryFactory
                .select(recruitment.postId,
                        recruitment.recruitmentId,
                        recruitment.positionId,
                        recruitment.stackId)
                .from(recruitment)
                .where(recruitment.postId.in(postIds))
                .fetch();


        Map<Long, PostListResponse.RecruitmentSummary> recruitmentMap = new HashMap<>();

        for (Tuple tuple : recruitmentTuples) {
            Long recId = tuple.get(recruitment.recruitmentId);
            Long posId = tuple.get(recruitment.positionId);
            Long stack = tuple.get(recruitment.stackId);

            if (recId == null) continue;

            PostListResponse.RecruitmentSummary summary =
                    recruitmentMap.computeIfAbsent(recId,
                            id -> new PostListResponse.RecruitmentSummary(
                                    recId,
                                    posId,
                                    stack
                            ));
        }


        Map<Long, List<PostListResponse.RecruitmentSummary>> postRecruitments =
                recruitmentTuples.stream()
                        .filter(t -> t.get(recruitment.postId) != null &&
                                t.get(recruitment.recruitmentId) != null)
                        .map(t -> recruitmentMap.get(t.get(recruitment.recruitmentId)))
                        .filter(Objects::nonNull)
                        .collect(Collectors.groupingBy(r -> recruitmentTuples.stream()
                                .filter(t -> Objects.equals(t.get(recruitment.recruitmentId), r.recruitmentId()))
                                .findFirst()
                                .map(t -> t.get(recruitment.postId))
                                .orElse(-1L)
                        ));


        List<PostListResponse> results = posts.stream()
                .map(p -> new PostListResponse(
                        p.getId(),
                        p.getTitle(),
                        p.getMainCategory(),
                        p.getSubCategory(),
                        p.getCreatedAt(),
                        p.getUpdatedAt(),
                        p.getDeadline(),
                        p.getViewCount(),
                        p.getApplicationCount(),
                        postRecruitments.getOrDefault(p.getId(), Collections.emptyList())
                ))
                .toList();

        return new PageImpl<>(results, pageable, total != null ? total : 0);
    }

}
