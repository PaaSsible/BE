package com.paassible.recruitservice.position.entity;

import jakarta.persistence.*;

import lombok.Getter;

@Entity
@Table(name = "positions")
@Getter
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    protected Position() {}
}
