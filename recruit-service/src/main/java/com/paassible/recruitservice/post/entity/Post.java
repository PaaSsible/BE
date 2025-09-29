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
    @Enumerated(EnumType.STRING)
    private MainCategory mainCategory;
    @Enumerated(EnumType.STRING)
    private SubCategory subCategory;
    private String title;
    private Long writerId;
    private String content;
    private LocalDate deadline;
    @Column(nullable = false)
    private boolean closed = false;

    @Enumerated(EnumType.STRING)
    private ProjectDuration months;

    @Column(nullable = false)
    private int viewCount = 0;
    @Column(nullable = false)
    private int applicationCount = 0;


    protected Post() {}

    private Post(MainCategory mainCategory,SubCategory subCategory, Long writerId, String title, String content, LocalDate deadline, ProjectDuration months) {
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.writerId = writerId;
        this.title = title;
        this.content = content;
        this.deadline = deadline;
        this.months = months;
    }

    public static Post create(MainCategory mainCategory,SubCategory subCategory, Long writerId, String title, String content, LocalDate deadline, ProjectDuration months) {
        return new Post(mainCategory, subCategory,  writerId, title, content, deadline, months);
    }

    public void updatePost(MainCategory mainCategory, SubCategory subCategory, String title, String content, LocalDate deadline, ProjectDuration months) {
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.title = title;
        this.content = content;
        this.deadline = deadline;
        this.months = months;
    }

    public void close(){
        this.closed = true;
    }

}
