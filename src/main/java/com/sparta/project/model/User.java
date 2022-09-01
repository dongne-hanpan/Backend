package com.sparta.project.model;
<<<<<<< HEAD

import lombok.Builder;
import lombok.Getter;
=======
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
>>>>>>> 7bff73339308dfd366848248ea984a0c18e5b94d

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

<<<<<<< HEAD
=======

>>>>>>> 7bff73339308dfd366848248ea984a0c18e5b94d
    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Builder
<<<<<<< HEAD
    public User(String username, String password, String nickname, Authority authority) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.authority = authority;
    }

=======
    public User(String nickname, String username, String password, Authority authority) {
        this.nickname = nickname;
        this.username = username;
        this.password = password;
        this.authority = authority;
    }
>>>>>>> 7bff73339308dfd366848248ea984a0c18e5b94d
}
