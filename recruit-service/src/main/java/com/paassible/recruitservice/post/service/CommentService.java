package com.paassible.recruitservice.post.service;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import com.paassible.recruitservice.post.dto.CommentCreateRequest;
import com.paassible.recruitservice.post.dto.CommentListResponse;
import com.paassible.recruitservice.post.dto.CommentResponse;
import com.paassible.recruitservice.post.entity.Comment;
import com.paassible.recruitservice.post.repository.CommentRepository;
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

    public void createComment(Long postId, CommentCreateRequest request,Long userId) {

        postRepository.findById(postId).orElseThrow(
                ()->new CustomException(ErrorCode.POST_NOT_FOUND));

        if(request.parentId() != null){
            Comment parent = commentRepository.findById(request.parentId())
                    .orElseThrow(()-> new CustomException(ErrorCode.PARENT_COMMENT_NOT_FOUND));

            if(parent.getDepth()>=1){
                throw new CustomException(ErrorCode.INVALID_COMMENT_DEPTH);
            }
        }
        Comment comment = Comment.create(
                request.content(),
                userId,
                postId,
                request.parentId()
        );

        commentRepository.save(comment);
    }

    public CommentListResponse getComments(Long postId) {
        List<Comment> parents = commentRepository.findByPostIdAndParentIdIsNull(postId);

        List<CommentResponse> responses = parents.stream()
                .map(parent ->{
                    List<Comment> children = commentRepository.findByParentId(parent.getId());
                    List<CommentResponse> childResponses = children.stream()
                            .map(child->CommentResponse.from(child,List.of()))
                            .toList();
                    return CommentResponse.from(parent, childResponses);
                })
                .toList();

        return new CommentListResponse(parents.size(), responses);
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
