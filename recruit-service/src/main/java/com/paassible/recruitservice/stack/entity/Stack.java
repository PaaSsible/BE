package com.paassible.recruitservice.stack.entity;

import jakarta.persistence.*;

import lombok.Getter;

@Entity
@Table(name = "stacks")
@Getter
public class Stack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    protected Stack() {}
}
