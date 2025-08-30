package com.paassible.recruitservice.post.entity;

import com.paassible.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Table(name = "post")
@Getter
public class Post extends BaseEntity{


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Long writerId;
    private String content;
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    private ProjectDuration months;


    protected Post() {}

    private Post(Long writerId, String title, String content, LocalDate deadline, ProjectDuration months) {
        this.writerId = writerId;
        this.title = title;
        this.content = content;
        this.deadline = deadline;
        this.months = months;
    }

    public static Post create(Long writerId, String title, String content, LocalDate deadline, ProjectDuration months) {
        return new Post(writerId, title, content, deadline, months);
    }

    public void updatePost(String title, String content, LocalDate deadline, ProjectDuration months) {
        this.title = title;
        this.content = content;
        this.deadline = deadline;
        this.months = months;
    }

}
