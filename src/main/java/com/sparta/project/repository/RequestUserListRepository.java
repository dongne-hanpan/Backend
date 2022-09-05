package com.sparta.project.repository;

import com.sparta.project.model.RequestUserList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestUserListRepository extends JpaRepository<RequestUserList, Long> {

    RequestUserList findByNickname(String nickname);

}
