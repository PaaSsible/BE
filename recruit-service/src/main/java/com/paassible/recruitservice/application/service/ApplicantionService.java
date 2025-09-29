package com.paassible.recruitservice.application.service;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import com.paassible.recruitservice.application.dto.AcceptRequest;
import com.paassible.recruitservice.application.dto.ApplicantResponse;
import com.paassible.recruitservice.application.dto.RejectRequest;
import com.paassible.recruitservice.application.entity.Application;
import com.paassible.recruitservice.application.entity.ApplicationStatus;
import com.paassible.recruitservice.application.repository.ApplicantionRepository;
import com.paassible.recruitservice.client.BoardClient;
import com.paassible.recruitservice.post.entity.Post;
import com.paassible.recruitservice.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicantionService {

    private final ApplicantionRepository applicationRepository;
    private final PostRepository postRepository;
    private final BoardClient boardClient;

    @Transactional
    public void apply(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(()->new CustomException(ErrorCode.POST_NOT_FOUND));

        if(applicationRepository.existsByPostIdAndApplicantId(postId, userId)){
            throw new CustomException(ErrorCode.APPLICATION_ALREADY_EXISTS);
        }

        if(post.getWriterId().equals(userId)){
            throw new CustomException(ErrorCode.CANNOT_APPLY_TO_OWN_POST);
        }

        Application application = Application.create(postId, userId);
        applicationRepository.save(application);

        post.increaseApplicationCount();
    }

    @Transactional(readOnly = true)
    public List<ApplicantResponse> getApplicants(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new CustomException(ErrorCode.POST_NOT_FOUND));

        if(!post.getWriterId().equals(userId)){
            throw new CustomException(ErrorCode.APPLICATION_UNAUTHORIZED);
        }

        List<Application> applications = applicationRepository.findAllByPostIdAndStatus(postId, ApplicationStatus.PENDING);

        return applications.stream()
                .map(ApplicantResponse::from)
                .toList();

    }

    @Transactional
    public void reject(Long postId, Long applicantId, RejectRequest rejectRequest, Long userId){
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new CustomException(ErrorCode.POST_NOT_FOUND));

        if(!post.getWriterId().equals(userId)){
            throw new CustomException(ErrorCode.APPLICATION_UNAUTHORIZED);
        }

        Application application = applicationRepository.findById(applicantId)
                .orElseThrow(()->new CustomException(ErrorCode.APPLICATION_NOT_FOUND));
        if(!application.getPostId().equals(postId)){
            throw new CustomException(ErrorCode.APPLICATION_MISMATCH);
        }

        application.reject(rejectRequest);
    }

    @Transactional
    public void accept(Long postId, Long applicationId, Long userId, AcceptRequest acceptRequest) {

        Post post = postRepository.findById(postId)
                .orElseThrow(()->new CustomException(ErrorCode.POST_NOT_FOUND));

        if(!post.getWriterId().equals(userId)){
            throw new CustomException(ErrorCode.APPLICATION_UNAUTHORIZED);
        }

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(()->new CustomException(ErrorCode.APPLICATION_NOT_FOUND));
        if(!application.getPostId().equals(postId)){
            throw new CustomException(ErrorCode.APPLICATION_MISMATCH);
        }

        boardClient.existUserInBoard(acceptRequest.boardId(),userId);

        application.accept();

        boardClient.addMember(acceptRequest.boardId(), application.getApplicantId());
    }



}
