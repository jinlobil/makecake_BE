package com.project.makecake.security.oauth.provider;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getNickname();
}
