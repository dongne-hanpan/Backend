package com.sparta.project.model;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Table(name = "users")
@Entity
@Getter
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
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
    public User(String nickname, String username, String password, Authority authority) {
        this.nickname = nickname;
        this.username = username;
        this.password = password;
        this.authority = authority;
    }
}
