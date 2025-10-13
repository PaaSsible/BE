package com.paassible.userservice.user.entity;

import com.paassible.common.security.jwt.Role;
import com.paassible.userservice.user.dto.ProfileRequest;
import com.paassible.userservice.user.entity.enums.DegreeType;
import com.paassible.userservice.user.entity.enums.GraduationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String socialId;

    private String email;

    private String profileImageUrl;

    private String nickname;

    private Long positionId;

    @ElementCollection
    @CollectionTable(name = "user_tech_stacks", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "tech_stack_id")
    private List<Long> stackIds = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private DegreeType degreeType;

    private String university;

    private String major;

    @Enumerated(EnumType.STRING)
    private GraduationStatus graduationStatus;

    private String introductionTitle;

    @Column(columnDefinition = "TEXT")
    private String introductionContent;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean agreedToTerms;

    private boolean deleted;

    public void updateRole(Role newRole) {
        this.role = newRole;
    }

    public void updateDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void updateAgreedToTerms(boolean agreedToTerms) {
        this.agreedToTerms = agreedToTerms;
    }

    public void updateProfile(ProfileRequest req, List<Long> stackIds, String imageUrl) {
        this.nickname = req.getNickname();
        this.positionId = req.getPositionId();
        this.stackIds.clear();
        this.stackIds.addAll(stackIds);
        this.degreeType = req.getDegreeType();
        this.university = req.getUniversity();
        this.major = req.getMajor();
        this.graduationStatus = req.getGraduationStatus();
        this.introductionTitle = req.getIntroductionTitle();
        this.introductionContent = req.getIntroductionContent();
        this.profileImageUrl = imageUrl;
    }
}
