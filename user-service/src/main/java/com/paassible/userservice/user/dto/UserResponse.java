package com.paassible.userservice.user.dto;

import com.paassible.userservice.user.entity.User;
import com.paassible.userservice.user.entity.enums.DegreeType;
import com.paassible.userservice.user.entity.enums.GraduationStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String nickname;
    private String email;
    private String profileImageUrl;

    private String positionName;
    private List<String> stackNames;

    private DegreeType degreeType;
    private String university;
    private String major;
    private GraduationStatus graduationStatus;

    private String introductionTitle;
    private String introductionContent;

    private String role;

    public static UserResponse from(User user, String positionName, List<String> stackNames) {
        return UserResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .positionName(positionName)
                .stackNames(stackNames)
                .degreeType(user.getDegreeType())
                .university(user.getUniversity())
                .major(user.getMajor())
                .graduationStatus(user.getGraduationStatus())
                .introductionTitle(user.getIntroductionTitle())
                .introductionContent(user.getIntroductionContent())
                .role(user.getRole().name())
                .build();
    }
}