package com.paassible.recruitservice.post.repository;

import com.paassible.recruitservice.post.dto.PostListResponse;
import com.paassible.recruitservice.post.dto.PostSearchRequest;
import com.paassible.recruitservice.post.entity.Post;
import com.paassible.recruitservice.post.entity.QPost;
import com.paassible.recruitservice.post.entity.QRecruitment;
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

        List<Post> posts;
        Long total;

        if(request.position()!= null){
            posts = queryFactory
                    .selectFrom(post)
                    .join(recruitment).on(post.id.eq(recruitment.postId))
                    .where(
                            recruitment.positionId.eq(request.position().longValue()),
                            request.mainCategory() != null? post.mainCategory.eq(request.mainCategory()) : null,
                            request.subCategory() !=  null? post.subCategory.eq(request.subCategory()) : null,
                            hasText(request.keyword()) ? post.title.containsIgnoreCase(request.keyword()): null
                    )
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .orderBy(orderSpecifier)
                    .fetch();

            total = queryFactory
                    .select(post.countDistinct())
                    .from(post)
                    .join(recruitment).on(post.id.eq(recruitment.postId))
                    .where(
                            recruitment.positionId.eq(request.position().longValue()),
                            request.mainCategory() != null? post.mainCategory.eq(request.mainCategory()) : null,
                            request.subCategory() != null? post.subCategory.eq(request.subCategory()) : null,
                            hasText(request.keyword()) ? post.title.containsIgnoreCase(request.keyword()) : null
                    )
                    .fetchOne();
        }else{
            posts = queryFactory
                    .selectFrom(post)
                    .where(
                            request.mainCategory() != null ? post.mainCategory.eq(request.mainCategory()) : null,
                            request.subCategory() != null? post.subCategory.eq(request.subCategory()) : null,
                            hasText(request.keyword()) ? post.title.containsIgnoreCase(request.keyword()) : null
                    )
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .orderBy(orderSpecifier)
                    .fetch();

            total = queryFactory
                    .select(post.count())
                    .from(post)
                    .where(
                            request.mainCategory() != null ? post.mainCategory.eq(request.mainCategory()) :  null,
                            request.subCategory() != null ? post.subCategory.eq(request.subCategory()) : null,
                            hasText(request.keyword()) ? post.title.containsIgnoreCase(request.keyword()) : null
                    )
                    .fetchOne();
        }
        return createPostListResponse(posts, pageable, total);
    }

    private Page<PostListResponse> createPostListResponse(List<Post> posts, Pageable pageable, Long total) {
        if(posts.isEmpty()){
            return new PageImpl<>(Collections.emptyList(),pageable,total != null? total : 0);
        }

        List<Long> postIds = posts.stream().map(Post::getId).collect(Collectors.toList());
        Map<Long, List<PostListResponse.RecruitmentSummary>> postRecruitments =
                getRecruitmentsByPostIds(postIds);
        List<PostListResponse> results = posts.stream()
                .map(p->new PostListResponse(
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
        return new PageImpl<>(results, pageable, total != null? total : 0);
    }

    private Map<Long,List<PostListResponse.RecruitmentSummary>> getRecruitmentsByPostIds(List<Long> postIds) {
        QRecruitment recruitment = QRecruitment.recruitment;

        return queryFactory
                .select(recruitment.postId, recruitment.recruitmentId,
                        recruitment.positionId, recruitment.stackId)
                .from(recruitment)
                .where(recruitment.postId.in(postIds))
                .fetch()
                .stream()
                .filter(t->t.get(recruitment.postId)!= null)
                .collect(Collectors.groupingBy(
                        t->Objects.requireNonNull(t.get(recruitment.postId)),
                        Collectors.mapping(
                                t->new PostListResponse.RecruitmentSummary(
                                        t.get(recruitment.recruitmentId),
                                        t.get(recruitment.positionId),
                                        t.get(recruitment.stackId)
                                ),
                                Collectors.toList()
                        )
                ));
    }

    private boolean hasText(String text){
        return text != null && !text.trim().isEmpty();
    }

}
