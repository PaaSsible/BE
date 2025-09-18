package com.paassible.recruitservice.post.entity;

import com.paassible.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "comments")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private Long writerId;

    @Column(nullable = false)
    private Long postId;

    private Long parentId;

    private int depth;

    private boolean deleted;

    public void markAsDeleted() {
        this.deleted = true;
    }

    protected Comment() {}

    private Comment(String content, Long writerId, Long postId, Long parentId, int depth) {
        this.content = content;
        this.writerId = writerId;
        this.postId = postId;
        this.parentId = parentId;
        this.depth = depth;
        this.deleted = false;
    }

    public static Comment create(String content, Long writerId, Long postId, Long parentId) {
        int depth = (parentId == null) ? 0 : 1;
        return new Comment(content, writerId, postId, parentId, depth);
    }
    public void updateContent(String newContent) {
        if (!this.deleted) {
            this.content = newContent;
        }
    }

}
