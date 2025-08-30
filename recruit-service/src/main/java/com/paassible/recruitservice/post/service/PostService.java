package com.paassible.recruitservice.post.service;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import com.paassible.recruitservice.position.entity.Position;
import com.paassible.recruitservice.position.repositoty.PositionRepository;
import com.paassible.recruitservice.post.dto.*;
import com.paassible.recruitservice.post.entity.Post;
import com.paassible.recruitservice.post.entity.Recruitment;
import com.paassible.recruitservice.post.repository.PostRepository;
import com.paassible.recruitservice.post.repository.RecruitmentRepository;
import com.paassible.recruitservice.stack.entity.Stack;
import com.paassible.recruitservice.stack.repositoty.StackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PositionRepository positionRepository;
    private final StackRepository stackRepository;
    private final RecruitmentRepository recruitmentRepository;

    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId) {

        Post post = postRepository.findById(postId).orElseThrow(
                ()-> new CustomException(ErrorCode.POST_NOT_FOUND)
        );

        List<Recruitment> recs = recruitmentRepository.findByPostId(postId);

        List<RecruitmentInfo> recruitmentInfos = recs.stream()
                .collect(Collectors.groupingBy(
                        Recruitment::getPositionId,
                        Collectors.mapping(Recruitment::getStackId, Collectors.toList())
                ))
                .entrySet().stream()
                .map(e-> new RecruitmentInfo(e.getKey(), e.getValue()))
                .toList();

        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getDeadline(),
                post.getMonths(),
                post.getWriterId(),
                recruitmentInfos
        );
    }

    @Transactional
    public PostCreateResponse createPost(PostCreateRequest request,long userId) {

        Post post = Post.create(
                userId,
                request.title(),
                request.content(),
                request.deadline(),
                request.projectDuration());

        Post savedPost = postRepository.save(post);

        request.recruitment()
                .forEach(r->{
                    Position position = positionRepository.findById(r.position()).orElseThrow(
                            ()-> new CustomException(ErrorCode.INVALID_INPUT));

                    r.stacks()
                            .forEach(s->{
                                Stack stack = stackRepository.findById(s)
                                        .orElseThrow(()->new CustomException(ErrorCode.INVALID_INPUT));

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
        post.updatePost(request.title(), request.content(), request.deadline(), request.projectDuration());

        recruitmentRepository.deleteByPostId(postId);

        if(request.recruitment() != null) {
            request.recruitment().forEach(r->{
                Position position = positionRepository.findById(r.position()).orElseThrow(
                        () -> new CustomException(ErrorCode.INVALID_INPUT)
                );
                r.stacks().forEach(s->{
                    Stack stack = stackRepository.findById(s).orElseThrow(
                            () -> new CustomException(ErrorCode.INVALID_INPUT)
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
