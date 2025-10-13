package com.paassible.userservice.user.dto;

import com.paassible.userservice.user.entity.enums.DegreeType;
import com.paassible.userservice.user.entity.enums.GraduationStatus;
import lombok.Getter;

import java.util.List;

@Getter
public class ProfileRequest {
    private String nickname;
    private Long positionId;
    private List<Long> techStackIds;

    private DegreeType degreeType;
    private String university;
    private String major;
    private GraduationStatus graduationStatus;

    private String introductionTitle;
    private String introductionContent;
}