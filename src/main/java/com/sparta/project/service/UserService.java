package com.sparta.project.service;

import antlr.Token;
import com.amazonaws.services.kms.model.NotFoundException;
import com.sparta.project.dto.match.MatchResponseDto;
import com.sparta.project.dto.match.UserListInMatchDto;
import com.sparta.project.dto.message.MessageResponseDto;
import com.sparta.project.dto.user.CommentDto;
import com.sparta.project.dto.user.EvaluationDto;
import com.sparta.project.dto.user.MyPageResponseDto;
import com.sparta.project.entity.*;
import com.sparta.project.repository.*;
import com.sparta.project.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.xml.stream.events.Comment;
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
    private final AwsS3Service awsS3Service;

    public void evaluateUser(EvaluationDto evaluationDto, String token) {

        int count = 0;
        User user = authService.getUserByToken(token);

        if (user.getNickname().equals(evaluationDto.getNickname())) {
            throw new IllegalArgumentException("자기 자신은 평가할 수 없습니다");
        }

        if (evaluationRepository.existsByMatchIdAndNicknameAndUser(evaluationDto.getMatch_id(), evaluationDto.getNickname(), user)) {
            throw new IllegalArgumentException("이미 평가한 유저입니다.");
        }

        List<UserListInMatch> list = userListInMatchRepository.findAllByMatchId(evaluationDto.getMatch_id());
        for (UserListInMatch userListInMatch : list) {
            if (userListInMatch.getUser().getNickname().equals(evaluationDto.getNickname()) || userListInMatch.getUser().getUsername().equals(user.getUsername())) {
                count++;
            }
        }
        if (count == 2) {
            evaluationRepository.save(Evaluation.builder()
                    .nickname(evaluationDto.getNickname())
                    .user(user)
                    .comment(evaluationDto.getComment())
                    .mannerPoint(evaluationDto.getMannerPoint())
                    .match_id(evaluationDto.getMatch_id())
                    .build());

        } else {
            throw new IllegalArgumentException("평가할 권한이 없습니다.");
        }
        count = 0;
    }

    public MyPageResponseDto myPage(String sports, String token) {

        User user = authService.getUserByToken(token);

        List<Bowling> bowling = bowlingRepository.findAllByUser(user);

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
                            .mannerPoint_HOST(calculateService.calculateMannerPoint(userRepository.findByNickname(invitedUser.getMatch().getWriter())))
                            .profileImage_HOST(userRepository.findByNickname(invitedUser.getMatch().getWriter()).getProfileImage())
                            .matchStatus(invitedUser.getMatch().getMatchStatus())
                            .userListInMatch(userList)
                            .matchCnt_HOST(bowling.size())
                            .averageScore_HOST(calculateService.calculateAverageScore(userRepository.findByNickname(invitedUser.getMatch().getWriter())))
                            .build());
            }


            return MyPageResponseDto.builder()
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

                list.add(MessageResponseDto.builder()
                        .chatId(match.getMatch().getId())
                        .profileImage(userRepository.findByNickname(match.getMatch().getWriter()).getProfileImage())
                        .hostNickname(userRepository.findByNickname(match.getMatch().getWriter()).getNickname())
                        .matchStatus(match.getMatch().getMatchStatus())
                        .date(match.getMatch().getDate())
                        .time(match.getMatch().getTime())
                        .place(match.getMatch().getPlace())
                        .build());

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

    public List<CommentDto> showComment(String nickname) {
        List<CommentDto> commentList = new ArrayList<>();

        for (Evaluation evaluation : evaluationRepository.findAllByNickname(nickname)) {

            commentList.add(CommentDto.builder()
                    .comment(evaluation.getComment())
                    .nickname(nickname)
                    .writer(evaluation.getUser().getNickname())
                    .build());
        }
        return commentList;
    }
}