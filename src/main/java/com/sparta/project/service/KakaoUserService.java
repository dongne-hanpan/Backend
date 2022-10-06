package com.sparta.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.project.dto.token.TokenDto;
import com.sparta.project.dto.user.KakaoLoginResponseDto;
import com.sparta.project.dto.user.KakaoUserInfoDto;
import com.sparta.project.dto.user.LoginRequestDto;
import com.sparta.project.dto.user.LoginResponseDto;
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
    private final AuthService authService;
    private final CalculateService calculateService;

    public KakaoLoginResponseDto kakaoLogin(String code) throws JsonProcessingException {

        String accessToken = getAccessToken(code);
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);
        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

        LoginResponseDto loginResponseDto = authService.login(LoginRequestDto.builder()
                .username(kakaoUserInfo.getId().toString())
                .password(kakaoUserInfo.getId() + "sparta")
                .build());

        return KakaoLoginResponseDto.builder()
                .grantType(loginResponseDto.getGrantType())
                .accessToken(loginResponseDto.getAccessToken())
                .username(kakaoUser.getUsername())
                .nickname(kakaoUser.getNickname())
                .mannerPoint(calculateService.calculateMannerPoint(kakaoUser))
                .profileImage(kakaoUser.getProfileImage())
                .build();

    }

    private String getAccessToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "c22ca4a980d7fc2f620f5b8a0a37e820");
        body.add("redirect_uri", "http://dongne-hanpan.com/user/kakao/callback");
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();

        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");


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
        String kakaoId = kakaoUserInfo.getId().toString();
        User kakaoUser = userRepository.findByUsername(kakaoId)
                .orElse(null);
        if (kakaoUser == null) {

            if(userRepository.existsByNickname(kakaoUserInfo.getNickname())) {
                String kakaoNickname = kakaoUserInfo.getNickname() + "A";

                return userRepository.save(User.builder()
                        .username(kakaoId)
                        .nickname(kakaoNickname)
                        .password(passwordEncoder.encode(kakaoUserInfo.getId() + "sparta"))
                        .authority(Authority.ROLE_USER)
                        .thumbnailImage(kakaoUserInfo.getThumbnailImage())
                        .build());
            }

            return userRepository.save(User.builder()
                    .username(kakaoId)
                    .nickname(kakaoUserInfo.getNickname())
                    .password(passwordEncoder.encode(kakaoUserInfo.getId() + "sparta"))
                    .authority(Authority.ROLE_USER)
                    .thumbnailImage(kakaoUserInfo.getThumbnailImage())
                    .build());
        }
        return kakaoUser;

    }


}