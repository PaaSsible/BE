package com.paassible.common.security.dto;

import com.paassible.common.security.jwt.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@Getter
@AllArgsConstructor
public class UserJwtDto {
    private Long userId;
    private Role role;
    private boolean agreedToTerms;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}