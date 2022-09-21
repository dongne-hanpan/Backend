package com.sparta.project.util;

import com.sparta.project.entity.User;
import com.sparta.project.repository.UserRepository;
import com.sparta.project.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class Scheduler {

    private final UserRepository userRepository;
    private final AwsS3Service awsS3Service;


    //사용되지 않는 사진을 S3에서 삭제하는 스케쥴러
    @Scheduled(cron = "0 0 0 * * *") // 초, 분, 시, 일, 월, 주 순서
    public void deleteS3File() {

        List<String> s3List = awsS3Service.S3FileList(); // s3에 있는 파일 리스트
        List<String> deleteList = new ArrayList<>(); // 삭제할 목록을 담을 리스트
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
                if(userRepository.findAll().get(i).getProfileImage() != null) {
                    if (!s.substring(0,37).equals(userRepository.findAll().get(i).getProfileImage().substring(50,87))) {
                        count++;
                    }
                }
            }

            if(count == DB_size) {
                deleteList.add(s);
            }

            count = 0;
        }

        for (String s : deleteList) {
            awsS3Service.deleteS3(s);
        }
        log.info("사용하지 않는 파일이 삭제되었습니다.");
    }
}
