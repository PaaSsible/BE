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
    private String mainCategory;
    private String subCategory;
    private String title;
    private Long writerId;
    private String content;
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    private ProjectDuration months;


    protected Post() {}

    private Post(String mainCategory, String subCategory, Long writerId, String title, String content, LocalDate deadline, ProjectDuration months) {
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.writerId = writerId;
        this.title = title;
        this.content = content;
        this.deadline = deadline;
        this.months = months;
    }

    public static Post create(String mainCategory, String subCategory, Long writerId, String title, String content, LocalDate deadline, ProjectDuration months) {
        return new Post(mainCategory, subCategory,  writerId, title, content, deadline, months);
    }

    public void updatePost(String mainCategory, String subCategory, String title, String content, LocalDate deadline, ProjectDuration months) {
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.title = title;
        this.content = content;
        this.deadline = deadline;
        this.months = months;
    }

}
