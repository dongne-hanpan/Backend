package com.sparta.project.model;

import com.sparta.project.dto.AverageDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Table(name = "users")
@Entity
@Getter
@Setter
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
    private int matchCount; // 볼링 게임 수

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

    @Builder
    public void updateMyScore(Long id, Long totalAverage) {
        this.id = id;
        this.totalAverage = totalAverage;
    }

}
