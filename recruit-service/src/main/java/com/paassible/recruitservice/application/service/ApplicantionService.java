package com.paassible.recruitservice.application.service;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import com.paassible.recruitservice.application.dto.*;
import com.paassible.recruitservice.application.entity.Application;
import com.paassible.recruitservice.application.entity.ApplicationStatus;
import com.paassible.recruitservice.application.repository.ApplicantionRepository;
import com.paassible.recruitservice.client.BoardClient;
import com.paassible.recruitservice.post.dto.RecruitInfo;
import com.paassible.recruitservice.post.entity.Post;
import com.paassible.recruitservice.post.entity.Recruitment;
import com.paassible.recruitservice.post.repository.PostRepository;
import com.paassible.recruitservice.post.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicantionService {

    private final ApplicantionRepository applicationRepository;
    private final PostRepository postRepository;
    private final BoardClient boardClient;
    private final RecruitmentRepository recruitmentRepository;

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

    @Transactional(readOnly = true)
    public List<MyApplicationListResponse> getMyApplications(Long userId) {

        List<Application> myApps = applicationRepository.findAllByApplicantIdOrderByUpdatedAtDesc(userId);

        List<Long> postIds = myApps.stream()
                .map(Application::getPostId)
                .toList();


        Map<Long, List<RecruitInfo>> recruitMap = getRecruitmentsByPostIds(postIds);

        return myApps.stream().map(app -> {
            Post post = postRepository.findById(app.getPostId())
                    .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

            List<RecruitInfo> recruits = recruitMap.getOrDefault(post.getId(), Collections.emptyList());

            return new MyApplicationListResponse(
                    app.getId(),
                    post.getId(),
                    post.getTitle(),
                    post.getMainCategory(),
                    post.getSubCategory(),
                    post.getCreatedAt(),
                    post.getUpdatedAt(),
                    post.getDeadline(),
                    post.getViewCount(),
                    post.getApplicationCount(),
                    recruits,
                    app.getStatus()
            );
        }).toList();
    }

    private Map<Long, List<RecruitInfo>> getRecruitmentsByPostIds(List<Long> postIds) {
        var recruitmentList = recruitmentRepository.findByPostIdIn(postIds);

        return recruitmentList.stream()
                .collect(Collectors.groupingBy(
                        Recruitment::getPostId,
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(
                                        Recruitment::getPositionId,
                                        Collectors.mapping(Recruitment::getStackId, Collectors.toList())
                                ),
                                map -> map.entrySet().stream()
                                        .map(e -> new RecruitInfo(e.getKey(), e.getValue()))
                                        .toList()
                        )
                ));
    }

    @Transactional
    public void cancel(Long applicationId, Long userId) {

        Application application = applicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_FOUND));

        if (application.getStatus() == ApplicationStatus.ACCEPTED) {
            throw new CustomException(ErrorCode.CANNOT_CANCEL_ACCEPTED_APPLICATION);
        }

        if(!application.getApplicantId().equals(userId)){
            throw new CustomException(ErrorCode.APPLICATION_UNAUTHORIZED_CANCEL);
        }

        applicationRepository.delete(application);
    }

}
