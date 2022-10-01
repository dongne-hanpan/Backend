package com.sparta.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.project.dto.token.TokenDto;
import com.sparta.project.dto.user.KakaoLoginResponseDto;
import com.sparta.project.dto.user.KakaoUserInfoDto;
import com.sparta.project.entity.Authority;
import com.sparta.project.entity.RefreshToken;
import com.sparta.project.entity.User;
import com.sparta.project.repository.RefreshTokenRepository;
import com.sparta.project.repository.UserRepository;
import com.sparta.project.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class KakaoUserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    private final CalculateService calculateService;
    public KakaoLoginResponseDto kakaoLogin(String code) throws JsonProcessingException, UnsupportedEncodingException {

        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code);

        // 2. "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        // 3. "카카오 사용자 정보"로 필요시 회원가입
        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

        // 4. 3단계에서 DB 중복검사가 된 유저 정보로 토큰 생성해서 dto에 담아서 retyrn
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(kakaoUser.getUsername(), kakaoUser.getPassword());

        //현재 로그인된 사람의 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        refreshTokenRepository.save(RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build());

        return KakaoLoginResponseDto.builder()
                .grantType(tokenDto.getGrantType())
                .accessToken(tokenDto.getAccessToken())
                .username(kakaoUser.getUsername())
                .nickname(kakaoUser.getNickname())
                .KakaoId(kakaoUser.getKakaoId())
                .mannerPoint(calculateService.calculateMannerPoint(kakaoUser))
                .profileImage(null)
                .build();

        // 4. 강제 로그인 처리
//        forceLogin(kakaoUser);
    }

    private String getAccessToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "7865af779df1a9975124d3cd86d9a5d4");
        body.add("redirect_uri", "http://3.38.191.6/user/kakao/callback");
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();

        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException, UnsupportedEncodingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();

        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();

        String thumbnailImage = jsonNode.get("properties")
                .get("thumbnail_image").asText();

        return new KakaoUserInfoDto(id, nickname, thumbnailImage);
    }

    private User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long kakaoId = kakaoUserInfo.getId();
        User kakaoUser = userRepository.findByKakaoId(kakaoId)
                .orElse(null);
        if (kakaoUser == null) {
            // 회원가입
            // username: kakao nickname

            return userRepository.save(User.builder()
                    .username(UUID.randomUUID().toString())
                    .nickname(kakaoUserInfo.getNickname())
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .authority(Authority.ROLE_USER)
                    .kakaoId(kakaoId)
                    .thumbnailImage(kakaoUserInfo.getThumbnailImage())
                    .build());

        }
        return kakaoUser;

    }


}