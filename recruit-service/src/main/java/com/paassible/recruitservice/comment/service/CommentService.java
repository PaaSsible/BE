package com.paassible.recruitservice.comment.service;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import com.paassible.recruitservice.client.UserClient;
import com.paassible.recruitservice.client.UserResponse;
import com.paassible.recruitservice.comment.dto.CommentCreateRequest;
import com.paassible.recruitservice.comment.dto.CommentListResponse;
import com.paassible.recruitservice.comment.dto.CommentResponse;
import com.paassible.recruitservice.comment.entity.Comment;
import com.paassible.recruitservice.comment.repository.CommentRepository;
import com.paassible.recruitservice.notification.PostNotificationPublisher;
import com.paassible.recruitservice.post.entity.Post;
import com.paassible.recruitservice.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    private final UserClient userClient;
    private final PostNotificationPublisher postNotificationPublisher;

    public void createComment(Long postId, CommentCreateRequest request, Long userId) {

        Post post = postRepository.findById(postId).orElseThrow(
                ()->new CustomException(ErrorCode.POST_NOT_FOUND));

        if(request.parentId() != null){
            Comment parent = commentRepository.findById(request.parentId())
                    .orElseThrow(()-> new CustomException(ErrorCode.PARENT_COMMENT_NOT_FOUND));

            if(parent.getDepth()>=1){
                throw new CustomException(ErrorCode.INVALID_COMMENT_DEPTH);
            }
        }

        UserResponse user = userClient.getUser(userId);
        Comment comment = Comment.create(
                request.content(),
                userId,
                user.getNickname(),
                user.getProfileImageUrl(),
                postId,
                request.parentId()
        );

        commentRepository.save(comment);

        postNotificationPublisher.sendComment(post.getWriterId(), post.getTitle());
    }

    public CommentListResponse getComments(Long postId, Long userId) {
        List<Comment> allComments = commentRepository.findByPostId(postId);

        List<Comment> parents = allComments.stream()
                .filter(c -> c.getParentId() == null)
                .toList();

        List<CommentResponse> responses = parents.stream()
                .map(parent -> {
                    List<CommentResponse> childResponses = allComments.stream()
                            .filter(c -> parent.getId().equals(c.getParentId()))
                            .map(child -> CommentResponse.from(child, List.of()))
                            .toList();

                    return CommentResponse.from(parent, childResponses);
                })
                .toList();

        return new CommentListResponse(userId,allComments.size(), responses);
    }


    public void updateComment(Long commentId, Long userId, String newContent){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        if(!comment.getWriterId().equals(userId)){
            throw new CustomException(ErrorCode.COMMENT_UPDATE_FORBIDDEN);
        }

        comment.updateContent(newContent);
    }

    public void deleteComment(Long commentId, Long userId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        if(!comment.getWriterId().equals(userId)){
            throw new CustomException(ErrorCode.COMMENT_DELETE_FORBIDDEN);
        }

        comment.markAsDeleted();
    }


}
