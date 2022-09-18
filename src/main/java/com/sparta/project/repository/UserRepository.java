package com.sparta.project.repository;


import com.sparta.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    User findByNickname(String nickname);
    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
}
