package com.paassible.recruitservice.post.service;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import com.paassible.recruitservice.position.entity.Position;
import com.paassible.recruitservice.position.repositoty.PositionRepository;
import com.paassible.recruitservice.post.dto.PostCreateRequest;
import com.paassible.recruitservice.post.dto.PostCreateResponse;
import com.paassible.recruitservice.post.entity.Post;
import com.paassible.recruitservice.post.entity.Recruitment;
import com.paassible.recruitservice.post.repository.PostRepository;
import com.paassible.recruitservice.post.repository.RecruitmentRepository;
import com.paassible.recruitservice.stack.entity.Stack;
import com.paassible.recruitservice.stack.repositoty.StackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PositionRepository positionRepository;
    private final StackRepository stackRepository;
    private final RecruitmentRepository recruitmentRepository;

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
}
