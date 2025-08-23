package com.paassible.userservice.auth.oauth;

public interface OAuth2UserInfo {

    String getProviderId();

    String getEmail();

    String getName();
}
