package com.sparta.project.util;

import com.sparta.project.dto.match.MatchRequestDto;
import com.sparta.project.entity.Match;
import com.sparta.project.entity.User;
import com.sparta.project.repository.MatchRepository;
import com.sparta.project.repository.UserRepository;
import com.sparta.project.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class Scheduler {

    private final UserRepository userRepository;
    private final AwsS3Service awsS3Service;
    private final MatchRepository matchRepository;

    @Scheduled(cron = "0 0 1 * * *") // 초, 분, 시, 일, 월, 주 순서
    public void deleteS3File() {

        List<String> s3List = awsS3Service.S3FileList();
        List<String> deleteList = new ArrayList<>();
        List<User> DB_List = new ArrayList<>();

        int count = 0;
        for(User user : userRepository.findAll()) {
            if(user.getProfileImage() != null) {
                DB_List.add(user);
            }
        }

        int DB_size = DB_List.size();

        for (String s : s3List) {
            for (int i = 0; i < DB_size; i++) {
                if(userRepository.findAll().get(i).getProfileImage() != null && !userRepository.findAll().get(i).getProfileImage().contains("kakao")) {
                    if (!s.substring(0,30).equals(userRepository.findAll().get(i).getProfileImage().substring(50,80))) {
                        System.out.println(s);
                    }
                }
            }
        }
        log.info("사용하지 않는 파일이 삭제되었습니다.");
    }

    @Scheduled(cron = "0 0 0 * * *")
    private void changeMatchStatusToDone() throws ParseException {

        List<Match> matches = matchRepository.findAll();

        for(Match match : matches) {
            if(match.getMatchStatus().equals("reserved")) {

                String matchDate = match.getDate();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = format.parse(matchDate); 

                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.DATE, -3);

                if(date.compareTo(new Date(cal.getTimeInMillis())) == 0) {
                    MatchRequestDto matchRequestDto = new MatchRequestDto();
                    matchRequestDto.setMatchStatus("done");
                    match.changeStatus(matchRequestDto);
                }

            }
        }

    }

}
