package com.paassible.recruitservice.application.service;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import com.paassible.recruitservice.application.dto.ApplicantResponse;
import com.paassible.recruitservice.application.dto.RejectRequest;
import com.paassible.recruitservice.application.entity.Application;
import com.paassible.recruitservice.application.entity.ApplicationStatus;
import com.paassible.recruitservice.application.repository.ApplicantionRepository;
import com.paassible.recruitservice.post.entity.Post;
import com.paassible.recruitservice.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicantionService {

    private final ApplicantionRepository applicantionRepository;
    private final PostRepository postRepository;

    @Transactional
    public void apply(Long postId, Long userId) {

        postRepository.findById(postId)
                .orElseThrow(()->new CustomException(ErrorCode.POST_NOT_FOUND));

        if(applicantionRepository.existsByPostIdAndApplicantId(postId, userId)){
            throw new CustomException(ErrorCode.APPLICATION_ALREADY_EXISTS);
        }

        Application application = Application.create(postId, userId);
        applicantionRepository.save(application);
    }

    @Transactional(readOnly = true)
    public List<ApplicantResponse> getApplicants(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new CustomException(ErrorCode.POST_NOT_FOUND));

        if(!post.getWriterId().equals(userId)){
            throw new CustomException(ErrorCode.APPLICATION_UNAUTHORIZED);
        }

        List<Application> applications = applicantionRepository.findAllByPostIdAndStatus(postId, ApplicationStatus.PENDING);

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

        Application application = applicantionRepository.findById(applicantId)
                .orElseThrow(()->new CustomException(ErrorCode.APPLICATION_NOT_FOUND));
        if(!application.getPostId().equals(postId)){
            throw new CustomException(ErrorCode.APPLICATION_MISMATCH);
        }

        application.reject(rejectRequest);
    }



}
