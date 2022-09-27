package com.sparta.project.entity;

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
    private String profileImage;

    @Column
    private Long kakaoId;
    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Builder
    public User(String username, String nickname, String password, String thumbnailImage, Long kakaoId, Authority authority) {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.profileImage = thumbnailImage;
        this.kakaoId = kakaoId;
        this.authority = authority;
    }
    public void uploadImage(String url) {
        this.profileImage = url;
    }
}