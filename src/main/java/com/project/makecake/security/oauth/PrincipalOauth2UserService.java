package com.project.makecake.security.oauth;

import com.project.makecake.model.User;
import com.project.makecake.model.UserRoleEnum;
import com.project.makecake.repository.UserRepository;
import com.project.makecake.security.UserDetailsImpl;
import com.project.makecake.security.oauth.provider.FacebookUserInfo;
import com.project.makecake.security.oauth.provider.GoogleUserInfo;
import com.project.makecake.security.oauth.provider.KakaoUserInfo;
import com.project.makecake.security.oauth.provider.OAuth2UserInfo;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public PrincipalOauth2UserService(UserRepository userRepository, @Lazy BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 구글로부터 받은 userRequest 데이터에 대한 후 처리되는 함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")){
            System.out.println("google 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")){
            System.out.println("facebook 로그인 요청");
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
        }else if (userRequest.getClientRegistration().getRegistrationId().equals("kakao")){
            System.out.println("kakao 로그인 요청");
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        }

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId;
        String nickname = oAuth2UserInfo.getNickname();
        String password = passwordEncoder.encode(UUID.randomUUID().toString());
        String userPicture = "";
        UserRoleEnum role = UserRoleEnum.USER;

        User findUser = userRepository.findByUsername(username).orElse(null);

        if (findUser == null) {
            findUser = User.builder()
                    .username(username)
                    .nickname(nickname)
                    .password(password)
                    .userPicture(userPicture)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(findUser);
        }

        return new UserDetailsImpl(findUser, oAuth2User.getAttributes());
    }
}
