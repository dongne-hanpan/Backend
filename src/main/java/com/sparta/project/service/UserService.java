package com.sparta.project.service;

import com.sparta.project.dto.match.MatchResponseDto;
import com.sparta.project.dto.match.UserListInMatchDto;
import com.sparta.project.dto.message.MessageResponseDto;
import com.sparta.project.dto.user.EvaluationDto;
import com.sparta.project.dto.user.MyPageResponseDto;
import com.sparta.project.entity.*;
import com.sparta.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserListInMatchRepository userListInMatchRepository;
    private final EvaluationRepository evaluationRepository;
    private final BowlingRepository bowlingRepository;
    private final CalculateService calculateService;
    private final AuthService authService;
    private final MatchRepository matchRepository;
    private final MessageRepository messageRepository;
    private final AwsS3Service awsS3Service;

    public void evaluateUser(EvaluationDto evaluationDto, String token) {

        int count = 0;

        User user = authService.getUserByToken(token);

        List<UserListInMatch> list = userListInMatchRepository.findAllByMatchId(evaluationDto.getMatch_id());
        for (UserListInMatch userListInMatch : list) {
            if (userListInMatch.getUser().getNickname().equals(evaluationDto.getNickname()) || userListInMatch.getUser().getUsername().equals(user.getUsername())) {
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

    public MyPageResponseDto myPage(String sports, String token) {
        User user = authService.getUserByToken(token);

        List<String> comment = new ArrayList<>();

        // 입력된 코멘트 불러오기
        for (Evaluation evaluation : evaluationRepository.findAllByNickname(user.getNickname())) {
            comment.add(evaluation.getComment());
        }

        // 마이페이지 - 볼링
        if (sports.equals("bowling")) {
            List<MatchResponseDto> list = new ArrayList<>();
            for (UserListInMatch invitedUser : userListInMatchRepository.findAllByUser(user)) {

                List<UserListInMatchDto> userList = new ArrayList<>();

                for (UserListInMatch userListInMatch : userListInMatchRepository.findAllByMatchId(invitedUser.getMatch().getId())) {
                    userList.add(UserListInMatchDto.builder()
                            .nickname(userListInMatch.getUser().getNickname())
                            .build());
                }

                if (invitedUser.getMatch().getSports().equals(sports))
                    list.add(MatchResponseDto.builder()
                            .match_id(invitedUser.getMatch().getId())
                            .matchIntakeCnt(userListInMatchRepository.countByMatch(invitedUser.getMatch()))
                            .matchIntakeFull(invitedUser.getMatch().getMatchIntakeFull())
                            .contents(invitedUser.getMatch().getContents())
                            .date(invitedUser.getMatch().getDate())
                            .time(invitedUser.getMatch().getTime())
                            .place(invitedUser.getMatch().getPlace())
                            .placeDetail(invitedUser.getMatch().getPlaceDetail())
                            .region(invitedUser.getMatch().getRegion())
                            .sports(invitedUser.getMatch().getSports())
                            .writer(invitedUser.getMatch().getWriter())
                            .level_HOST(calculateService.calculateLevel(user))
                            .matchStatus(invitedUser.getMatch().getMatchStatus())
                            .userListInMatch(userList)
                            .build());
            }
            List<Bowling> bowling = bowlingRepository.findAllByUser(user);

            return MyPageResponseDto.builder()
                    .comment(comment)
                    .nickname(user.getNickname())
                    .mannerPoint(calculateService.calculateMannerPoint(user))
                    .matchCount(bowling.size())
                    .matchList(list)
                    .profileImage(user.getProfileImage())
                    .score(calculateService.calculateAverageScore(user))
                    .level(calculateService.calculateLevel(user))
                    .build();
        }
        return null;
    }

    public List<MessageResponseDto> myChatList(String token) {

        User user = authService.getUserByToken(token);

        List<UserListInMatch> matches = userListInMatchRepository.findAllByUser(user);

        List<MessageResponseDto> list = new ArrayList<>();

        try {
            for (UserListInMatch match : matches) {
                if (!match.getMatch().getMatchStatus().equals("done")) {
                    list.add(MessageResponseDto.builder()
                            .chatId(match.getMatch().getId())
                            .profileImage(userRepository.findByNickname(match.getMatch().getWriter()).getProfileImage())
                            .hostNickname(userRepository.findByNickname(match.getMatch().getWriter()).getNickname())
                            .date(match.getMatch().getDate())
                            .time(match.getMatch().getTime())
                            .place(match.getMatch().getPlace())
                            .build());
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }


    }

    @Transactional
    public String uploadProfileImage(MultipartFile multipartFile, String token) throws IOException {

        String imageUrl = awsS3Service.saveImageUrl(multipartFile);

        User user = authService.getUserByToken(token);

        user.uploadImage(imageUrl);

        return imageUrl;
    }
}