package com.project.makecake.security.oauth.provider;


public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getNickname();
}
