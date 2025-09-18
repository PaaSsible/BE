package com.paassible.recruitservice.post.service;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import com.paassible.recruitservice.client.UserClient;
import com.paassible.recruitservice.client.UserResponse;
import com.paassible.recruitservice.position.entity.Position;
import com.paassible.recruitservice.position.repositoty.PositionRepository;
import com.paassible.recruitservice.post.dto.*;
import com.paassible.recruitservice.post.dto.PagedPostListResponse;
import com.paassible.recruitservice.post.dto.PostListResponse;
import com.paassible.recruitservice.post.dto.PostSearchRequest;
import com.paassible.recruitservice.post.entity.Post;
import com.paassible.recruitservice.post.entity.QPost;
import com.paassible.recruitservice.post.entity.Recruitment;
import com.paassible.recruitservice.post.repository.PostRepository;
import com.paassible.recruitservice.post.repository.RecruitmentRepository;
import com.paassible.recruitservice.stack.entity.Stack;
import com.paassible.recruitservice.stack.repositoty.StackRepository;
import com.querydsl.core.types.OrderSpecifier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PositionRepository positionRepository;
    private final StackRepository stackRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final UserClient userClient;


    @Transactional(readOnly = true)
    public PagedPostListResponse getPosts(PostSearchRequest request) {
        Pageable pageable = PageRequest.of(request.page(), request.size());

        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(request.sort());

        Page<PostListResponse> postsPage = postRepository.searchPosts(request, pageable, orderSpecifier);

        var pageInfo = new PagedPostListResponse.PageInfo(
                postsPage.getNumber(),
                postsPage.getTotalPages(),
                postsPage.getTotalElements(),
                postsPage.getSize(),
                postsPage.hasNext()
        );

        return new PagedPostListResponse(postsPage.getContent(), pageInfo);
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort) {
        QPost post = QPost.post;

        return switch (sort.toUpperCase()) {
            case "RECENT" -> post.createdAt.desc();
            case "DEADLINE" -> post.deadline.asc();
            case "POPULAR" -> post.applicationCount.desc();
            default -> throw new CustomException(ErrorCode.INVALID_SORT_OPTION);
        };
    }


    public PagedPostListResponse getMyPosts(Long userId, Integer position, String sort, Pageable pageable) {

        Sort sortOption = switch (sort.toUpperCase()) {
            case "RECENT"   -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "DEADLINE" -> Sort.by(Sort.Direction.ASC, "deadline");
            case "POPULAR"  -> Sort.by(Sort.Direction.DESC, "applicationCount");
            default -> throw new CustomException(ErrorCode.INVALID_SORT_OPTION);
        };

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortOption
        );


        Page<Post> posts = (position != null)
                ? postRepository.findMyPostsByPosition(userId, position.longValue(), sortedPageable)
                : postRepository.findByWriterId(userId, sortedPageable);

        if (posts.isEmpty()) {
            return new PagedPostListResponse(Collections.emptyList(),
                    new PagedPostListResponse.PageInfo(
                            posts.getNumber(),
                            posts.getTotalPages(),
                            posts.getTotalElements(),
                            posts.getSize(),
                            posts.hasNext()
                    ));
        }

        List<Long> postIds = posts.getContent().stream().map(Post::getId).toList();
        List<Recruitment> recruitments = recruitmentRepository.findByPostIdIn(postIds);


        Map<Long, List<PostListResponse.RecruitmentSummary>> recruitmentsByPost =
                recruitments.stream()
                        .collect(Collectors.groupingBy(
                                Recruitment::getPostId,
                                Collectors.mapping(
                                        r -> new PostListResponse.RecruitmentSummary(
                                                r.getRecruitmentId(),
                                                r.getPositionId(),
                                                r.getStackId()
                                        ),
                                        Collectors.toList()
                                )
                        ));


        List<PostListResponse> results = posts.getContent().stream()
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
                        recruitmentsByPost.getOrDefault(p.getId(), Collections.emptyList())
                ))
                .toList();

        var pageInfo = new PagedPostListResponse.PageInfo(
                posts.getNumber(),
                posts.getTotalPages(),
                posts.getTotalElements(),
                posts.getSize(),
                posts.hasNext()
        );

        return new PagedPostListResponse(results, pageInfo);
    }




    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId) {

        Post post = postRepository.findById(postId).orElseThrow(
                ()-> new CustomException(ErrorCode.POST_NOT_FOUND)
        );

        List<Recruitment> recs = recruitmentRepository.findByPostId(postId);

        UserResponse user = userClient.getUser(post.getWriterId());

        List<RecruitmentInfo> recruitmentInfos = recs.stream()
                .collect(Collectors.groupingBy(
                        Recruitment::getPositionId,
                        Collectors.mapping(Recruitment::getStackId, Collectors.toList())
                ))
                .entrySet().stream()
                .map(e-> new RecruitmentInfo(e.getKey(), e.getValue()))
                .toList();

        return new PostDetailResponse(
                post.getMainCategory(),
                post.getSubCategory(),
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getDeadline(),
                post.getMonths(),
                post.getWriterId(),
                user.getNickname(),
                recruitmentInfos
        );
    }

    @Transactional
    public PostCreateResponse createPost(PostCreateRequest request,long userId) {

        Post post = Post.create(
                request.mainCategory(),
                request.subCategory(),
                userId,
                request.title(),
                request.content(),
                request.deadline(),
                request.projectDuration());

        Post savedPost = postRepository.save(post);

        request.recruitment()
                .forEach(r->{
                    Position position = positionRepository.findById(r.position()).orElseThrow(
                            ()-> new CustomException(ErrorCode.INVALID_POSITION));

                    r.stacks()
                            .forEach(s->{
                                Stack stack = stackRepository.findById(s)
                                        .orElseThrow(()->new CustomException(ErrorCode.INVALID_STACK));

                                Recruitment recruitment = Recruitment.create(
                                        savedPost.getId(),
                                        position.getId(),
                                        stack.getId()
                                );
                                recruitmentRepository.save(recruitment);
                            });
                });

        return  new PostCreateResponse(savedPost.getId());
    }

    @Transactional
    public PostUpdateResponse updatePost(Long postId,PostUpdateRequest request, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(
                ()-> new CustomException(ErrorCode.POST_NOT_FOUND));

        if(!post.getWriterId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACTION);
        }
        post.updatePost(request.mainCategory(), request.subCategory(), request.title(), request.content(), request.deadline(), request.projectDuration());

        recruitmentRepository.deleteByPostId(postId);

        if(request.recruitment() != null) {
            request.recruitment().forEach(r->{
                Position position = positionRepository.findById(r.position()).orElseThrow(
                        () -> new CustomException(ErrorCode.INVALID_POSITION)
                );
                r.stacks().forEach(s->{
                    Stack stack = stackRepository.findById(s).orElseThrow(
                            () -> new CustomException(ErrorCode.INVALID_STACK)
                    );
                    Recruitment recruitment = Recruitment.create(post.getId(), position.getId(), stack.getId());
                    recruitmentRepository.save(recruitment);
                });
            });
        }
        return  new PostUpdateResponse(post.getId());
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND)
        );
        if(!post.getWriterId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACTION);
        }

        recruitmentRepository.deleteByPostId(postId);
        postRepository.delete(post);
    }


}
