package com.project.makecake.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.makecake.dto.ImageInfoDto;
import com.project.makecake.dto.LoginCheckResponseDto;
import com.project.makecake.dto.MypageResponseDto;
import com.project.makecake.dto.SignupRequestDto;
import com.project.makecake.enums.FolderName;
import com.project.makecake.model.User;
import com.project.makecake.enums.UserRoleEnum;
import com.project.makecake.repository.UserRepository;
import com.project.makecake.security.JwtProperties;
import com.project.makecake.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
public class UserService {

    @Value("${google.client-id}")
    String googleClientId;

    @Value("${google.client-secret}")
    String googleClientSecret;

    @Value("${naver.client-id}")
    String naverClientId;

    @Value("${naver.client-secret}")
    String naverClientSecret;

    @Value("${kakao.client-id}")
    String kakaoClientId;

//    @Value("${kakao.client-secret}")
//    String kakaoClientSecret;

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;

    // 회원가입
    public HashMap<String, Boolean> registerUser(SignupRequestDto requestDto) {

        String username = requestDto.getUsername();

        Optional<User> usernameCheck = userRepository.findByUsername(username);
        if (usernameCheck.isPresent()) {
            throw new IllegalArgumentException("중복된 email이 존재합니다.");
        }

        String nickname = requestDto.getNickname();

        Optional<User> nicknameCheck = userRepository.findByNickname(nickname);
        if (nicknameCheck.isPresent()) {
            throw new IllegalArgumentException("중복된 닉네임이 존재합니다.");
        }

        if (!requestDto.getPassword().equals(requestDto.getPasswordCheck())) {
            throw new IllegalArgumentException("패스워드가 일치하지 않습니다.");
        }

        String password = passwordEncoder.encode(requestDto.getPassword());

        String profileImgUrl = "https://makecake.s3.ap-northeast-2.amazonaws.com/PROFILE/18d2090b-1b98-4c34-b92b-a9f50d03bd53makecake_default.png";

        UserRoleEnum role = UserRoleEnum.USER;

        User user = User.builder()
                .username(username)
                .nickname(nickname)
                .password(password)
                .profileImgUrl(profileImgUrl)
                .profileImgName(null)
                .role(role)
                .build();
        User saveUser = userRepository.save(user);
        Optional<User> checkUser = userRepository.findById(saveUser.getUserId());
        HashMap<String, Boolean> userCheck = new HashMap<>();
        userCheck.put("signup", checkUser.isPresent());
        return userCheck;
    }

    // username 중복검사
    public HashMap<String, Boolean> usernameCheck(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        Optional<User> sameUser = userRepository.findByUsername(username);
        HashMap<String, Boolean> usernameCheck = new HashMap<>();
        usernameCheck.put("isTrue", sameUser.isPresent());
        return usernameCheck;
    }

    // nickname 중복검사
    public HashMap<String, Boolean> nicknameCheck(SignupRequestDto requestDto) {
        String nickname = requestDto.getNickname();
        Optional<User> sameNickname = userRepository.findByNickname(nickname);
        HashMap<String, Boolean> nicknameCheck = new HashMap<>();
        nicknameCheck.put("isTrue", sameNickname.isPresent());
        return nicknameCheck;
    }

    // 로그인체크
    public LoginCheckResponseDto loginChecked(UserDetailsImpl userDetails) {
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        LoginCheckResponseDto loginCheck = LoginCheckResponseDto.builder()
                        .userId(findUser.getUserId())
                        .nickname(findUser.getNickname())
                        .build();
        System.out.println(loginCheck);
        return loginCheck;
    }

    // 회원 탈퇴
    public MypageResponseDto resignUser(UserDetailsImpl userDetails) {
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);

        if (findUser != null) {
            String username = "resignUser_"+findUser.getUserId();
            String password = passwordEncoder.encode(UUID.randomUUID().toString());
            String nickname = "알수없음";
            findUser.setUsername(username);
            findUser.setNickname(nickname);
            findUser.setPassword(password);
            findUser.setProfileImgUrl("https://makecake.s3.ap-northeast-2.amazonaws.com/PROFILE/18d2090b-1b98-4c34-b92b-a9f50d03bd53makecake_default.png");
            findUser.setProfileImgName(null);
            findUser.setRole(null);
            findUser.setProvider(null);
            findUser.setProviderEmail(null);
            findUser.setProviderId(null);
            userRepository.save(findUser);
        }
        String email = findUser.getUsername();
        if (findUser.getProviderEmail() != null){
            email = findUser.getProviderEmail();
        }
        MypageResponseDto mypage = MypageResponseDto.builder()
                .nickname(findUser.getNickname())
                .profileImg(findUser.getProfileImgUrl())
                .email(email)
                .build();
        return mypage;
    }

    // 프로필이미지 수정
    public MypageResponseDto editProfile(MultipartFile multipartFile, UserDetailsImpl userDetails) throws IOException {
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        // 기본 이미지일때는 profileImgName이 null이므로 null이 아닐때 s3에서 해당 이미지를 삭제한다.
        if (findUser.getProfileImgName() != null){
            s3UploadService.deleteFile(findUser.getProfileImgName());
        }

        ImageInfoDto imageInfoDto = s3UploadService.uploadFile(multipartFile, FolderName.PROFILE.name());

        findUser.setProfileImgName(imageInfoDto.getName());
        findUser.setProfileImgUrl(imageInfoDto.getUrl());
        User saveUser = userRepository.save(findUser);
        MypageResponseDto responseDto = MypageResponseDto.builder()
                .nickname(saveUser.getNickname())
                .profileImg(saveUser.getProfileImgUrl())
                .build();
        return responseDto;
    }

    // 닉네임 수정
    public MypageResponseDto editNickname(SignupRequestDto signupRequestDto, UserDetailsImpl userDetails) {
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        if (findUser.getNickname().equals(signupRequestDto.getNickname())){
            throw new IllegalArgumentException("중복된 닉네임이 존재합니다.");
        }
        findUser.setNickname(signupRequestDto.getNickname());
        User saveUser = userRepository.save(findUser);
        MypageResponseDto responseDto = MypageResponseDto.builder()
                .nickname(saveUser.getNickname())
                .profileImg(saveUser.getProfileImgUrl())
                .build();
        return responseDto;
    }

    // 카카오 로그인
    public void kakaoLogin(String code, HttpServletResponse response3) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        System.out.println(code);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        // 새로운 코드
        body.add("client_id", kakaoClientId);
//        body.add("client_secret", kakaoClientSecret);
        body.add("redirect_uri", "http://localhost:3000/user/kakao/callback");
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoToken = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST, kakaoToken, String.class);

        String responseBody = response.getBody();
        System.out.println(responseBody);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseToken = objectMapper.readTree(responseBody);
        String accessToken = responseToken.get("access_token").asText();

        HttpHeaders headers2 = new HttpHeaders();
        headers2.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + accessToken);

        HttpEntity<MultiValueMap<String, String>> kakaoUserInfo = new HttpEntity<>(headers2);
        RestTemplate restTemplate2 = new RestTemplate();
        ResponseEntity<String> response2 = restTemplate2.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST, kakaoUserInfo, String.class);

        String responseBody2 = response2.getBody();
        System.out.println(responseBody2);
        ObjectMapper objectMapper2 = new ObjectMapper();
        JsonNode responseInfo = objectMapper2.readTree(responseBody2);

        String providerId = responseInfo.get("id").asText();
//        String providerEmail = null;
//        if (!responseInfo.get("kakao_account").get("email").isNull()){
//            providerEmail = responseInfo.get("kakao_account").get("email").asText();
//        }
        String provider = "kakao";
        String username = provider + "_" + providerId;
        String nickname = provider + "_" + providerId;
        String password = passwordEncoder.encode(UUID.randomUUID().toString());
        String profileImgUrl = "https://makecake.s3.ap-northeast-2.amazonaws.com/PROFILE/18d2090b-1b98-4c34-b92b-a9f50d03bd53makecake_default.png";
        UserRoleEnum role = UserRoleEnum.USER;

        User findUser = userRepository.findByUsername(username).orElse(null);
        if (findUser == null){
            findUser = User.builder()
                    .username(username)
                    .nickname(nickname)
                    .password(password)
                    .profileImgUrl(profileImgUrl)
                    .profileImgName(null)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .providerEmail(null)
                    .build();
            userRepository.save(findUser);
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(findUser);
        System.out.println("kakao 로그인 완료 : " + userDetails.getUser().getUsername());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        // 강제로 시큐리티 세션에 접근하여 authentication 객체를 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = JWT.create()
                // 토큰이름
                .withSubject("JwtToken : " + userDetails.getUser().getUsername())
                // 유효시간
                .withClaim("expireDate", new Date(System.currentTimeMillis() + JwtProperties.tokenValidTime))
                // username
                .withClaim("username", userDetails.getUser().getUsername())
                // HMAC256 복호화
                .sign(Algorithm.HMAC256(JwtProperties.secretKey));
        System.out.println("jwtToken : " + jwtToken);
        response3.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
    }

    // 네이버 로그인
    public void naverLogin(String code, String state, HttpServletResponse response3) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", naverClientId);
        body.add("client_secret", naverClientSecret);
        body.add("code", code);
        body.add("state", state);

        HttpEntity<MultiValueMap<String, String>> naverToken = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange("https://nid.naver.com/oauth2.0/token", HttpMethod.POST, naverToken, String.class);

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseToken = objectMapper.readTree(responseBody);
        String accessToken = responseToken.get("access_token").asText();

        HttpHeaders headers2 = new HttpHeaders();
        headers2.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + accessToken);
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> naverUserInfo = new HttpEntity<>(headers2);
        RestTemplate restTemplate2 = new RestTemplate();
        ResponseEntity<String> response2 = restTemplate2.exchange("https://openapi.naver.com/v1/nid/me", HttpMethod.POST, naverUserInfo, String.class);

        String responseBody2 = response2.getBody();
        ObjectMapper objectMapper2 = new ObjectMapper();
        JsonNode responseInfo = objectMapper2.readTree(responseBody2);

        String providerId = responseInfo.get("response").get("id").asText();
        String providerEmail = responseInfo.get("response").get("email").asText();
        String provider = "naver";
        String username = provider + "_" + providerId;
        String nickname = provider + "_" + providerId;
        String password = passwordEncoder.encode(UUID.randomUUID().toString());
        String profileImgUrl = "https://makecake.s3.ap-northeast-2.amazonaws.com/PROFILE/18d2090b-1b98-4c34-b92b-a9f50d03bd53makecake_default.png";
        UserRoleEnum role = UserRoleEnum.USER;

        User findUser = userRepository.findByUsername(username).orElse(null);
        if (findUser == null) {
            findUser = User.builder()
                    .username(username)
                    .nickname(nickname)
                    .password(password)
                    .profileImgUrl(profileImgUrl)
                    .profileImgName(null)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .providerEmail(providerEmail)
                    .build();
            userRepository.save(findUser);
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(findUser);
        System.out.println("naver 로그인 완료 : " + userDetails.getUser().getUsername());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        // 강제로 시큐리티 세션에 접근하여 authentication 객체를 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = JWT.create()
                // 토큰이름
                .withSubject("JwtToken : " + userDetails.getUser().getUsername())
                // 유효시간
                .withClaim("expireDate", new Date(System.currentTimeMillis() + JwtProperties.tokenValidTime))
                // username
                .withClaim("username", userDetails.getUser().getUsername())
                // HMAC256 복호화
                .sign(Algorithm.HMAC256(JwtProperties.secretKey));
        System.out.println("jwtToken : " + jwtToken);

        response3.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
    }

    // 구글 로그인
    public void google(String code, HttpServletResponse response3) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id" , googleClientId);
        body.add("client_secret", googleClientSecret);
        body.add("code", code);
        body.add("redirect_uri", "http://localhost:8080/user/google/callback");
        body.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> googleToken = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange("https://oauth2.googleapis.com/token", HttpMethod.POST, googleToken, String.class);

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseToken = objectMapper.readTree(responseBody);
        String accessToken = responseToken.get("access_token").asText();

        HttpHeaders headers2 = new HttpHeaders();
        headers2.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> googleUserInfo = new HttpEntity<>(headers2);
        RestTemplate restTemplate2 = new RestTemplate();
        ResponseEntity<String> response2 = restTemplate2.exchange("https://openidconnect.googleapis.com/v1/userinfo", HttpMethod.POST, googleUserInfo, String.class);

        String responseBody2 = response2.getBody();
        ObjectMapper objectMapper2 = new ObjectMapper();
        JsonNode responseInfo = objectMapper2.readTree(responseBody2);

        String providerId = responseInfo.get("sub").asText();
        String providerEmail = responseInfo.get("email").asText();
        String provider = "google";
        String username = provider + "_" + providerId;
        String nickname = provider + "_" + providerId;
        String password = passwordEncoder.encode(UUID.randomUUID().toString());
        String profileImgUrl = "https://makecake.s3.ap-northeast-2.amazonaws.com/PROFILE/18d2090b-1b98-4c34-b92b-a9f50d03bd53makecake_default.png";
        UserRoleEnum role = UserRoleEnum.USER;

        User findUser = userRepository.findByUsername(username).orElse(null);
        if (findUser == null) {
            findUser = User.builder()
                    .username(username)
                    .nickname(nickname)
                    .password(password)
                    .profileImgUrl(profileImgUrl)
                    .profileImgName(null)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .providerEmail(providerEmail)
                    .build();
            userRepository.save(findUser);
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(findUser);
        System.out.println("google 로그인 완료 : " + userDetails.getUser().getUsername());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        // 강제로 시큐리티 세션에 접근하여 authentication 객체를 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = JWT.create()
                // 토큰이름
                .withSubject("JwtToken : " + userDetails.getUser().getUsername())
                // 유효시간
                .withClaim("expireDate", new Date(System.currentTimeMillis() + JwtProperties.tokenValidTime))
                // username
                .withClaim("username", userDetails.getUser().getUsername())
                // HMAC256 복호화
                .sign(Algorithm.HMAC256(JwtProperties.secretKey));
        System.out.println("jwtToken : " + jwtToken);

        response3.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
    }
}
