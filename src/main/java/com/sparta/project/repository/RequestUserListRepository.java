package com.sparta.project.repository;

import com.sparta.project.model.Match;
import com.sparta.project.model.RequestUserList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestUserListRepository extends JpaRepository<RequestUserList, Long> {

    RequestUserList findByNickname(String nickname);
    List<RequestUserList> findAllByMatch(Match match);

}
