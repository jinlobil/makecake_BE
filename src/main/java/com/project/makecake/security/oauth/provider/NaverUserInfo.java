package com.project.makecake.security.oauth.provider;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class NaverUserInfo implements OAuth2UserInfo{

    private final Map<String, Object> attributes;

    @Override
    public String getProviderId() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return (String) response.get("id");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getNickname() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return (String) response.get("nickname");
    }

    @Override
    public String getProviderEmail() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return (String) response.get("email");
    }
}
