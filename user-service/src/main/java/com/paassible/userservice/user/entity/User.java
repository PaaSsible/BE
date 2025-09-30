package com.paassible.userservice.user.entity;

import com.paassible.common.security.jwt.Role;
import jakarta.persistence.*;
import lombok.*;

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

    private String nickname;

    private String major;

    private String profileImageUrl;

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

    public void updateAgreedToTerms(boolean termsAgreed) {
        this.agreedToTerms = termsAgreed;
    }
}
