package com.sparta.project.repository;

import com.sparta.project.entity.Match;
import com.sparta.project.entity.RequestUserList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestUserListRepository extends JpaRepository<RequestUserList, Long> {

    RequestUserList findByNicknameAndMatch(String nickname, Match match);
    List<RequestUserList> findAllByMatch(Match match);
    boolean existsByNicknameAndMatch(String nickname, Match match);

}
