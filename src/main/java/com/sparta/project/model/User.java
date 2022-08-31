package com.sparta.project.model;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Table(name = "users")
@Entity
@Getter
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private String nickname;

    @Column
    private Long totalAverage;  // 볼링 평균 평점

    @Column
    private Long matchCount; // 볼링 게임 수

    @Column
    private String profileImage;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Builder
    public User(String username, String password, String nickname, Authority authority) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.authority = authority;
    }

}
