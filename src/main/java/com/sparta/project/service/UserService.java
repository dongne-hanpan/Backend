package com.sparta.project.service;

import com.sparta.project.dto.AverageDto;
import com.sparta.project.dto.EvaluationDto;
import com.sparta.project.dto.UserResponseDto;
import com.sparta.project.model.Evaluation;
import com.sparta.project.model.InvitedUser;
import com.sparta.project.model.User;
import com.sparta.project.repository.EvaluationRepository;
import com.sparta.project.repository.InvitedUserRepository;
import com.sparta.project.repository.UserRepository;
import com.sparta.project.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final InvitedUserRepository invitedUserRepository;
    private final EvaluationRepository evaluationRepository;

    @Transactional(readOnly = true)
    public UserResponseDto getUserInfo(String username) {
        return userRepository.findByUsername(username)
                .map(UserResponseDto::of)
                .orElseThrow(() -> new RuntimeException("유저 정보가 없습니다."));
    }

    // 현재 SecurityContext 에 있는 유저 정보 가져오기
    @Transactional(readOnly = true)
    public UserResponseDto getMyInfo() {
        return userRepository.findById(SecurityUtil.getCurrentUserId())
                .map(UserResponseDto::of)
                .orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다."));
    }

    public void evaluateUser(EvaluationDto evaluationDto) {
        int count = 0;
        List<InvitedUser> list = invitedUserRepository.findAllByMatchId(evaluationDto.getMatch_id());
        for (InvitedUser invitedUser : list) {
            if (invitedUser.getUser().getNickname().equals(evaluationDto.getNickname()) || invitedUser.getUser().getUsername().equals(getMyInfo().getUsername())) {
                count++;
            }
        }

        if (count == 2) {
            evaluationRepository.save(Evaluation.builder()
                    .nickname(evaluationDto.getNickname())
                    .user(userRepository.findByNickname(evaluationDto.getNickname()))
                    .comment(evaluationDto.getComment())
                    .mannerPoint(evaluationDto.getMannerPoint())
                    .match_id(evaluationDto.getMatch_id())
                    .build());

        }
        count = 0;
    }

}