package com.paassible.userservice.auth.oauth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleUserInfo {
    private String sub;
    private String email;
    private String name;
    private String given_name;
    private String family_name;
    private String picture;
    private Boolean email_verified;
    private String locale;
}