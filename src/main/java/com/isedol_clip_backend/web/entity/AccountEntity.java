package com.isedol_clip_backend.web.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "ACCOUNT")
public class AccountEntity {
    @Id
    private long id;

    @Column(name = "TWITCH_ACCESS_TOKEN", length = 50)
    private String TwitchAccessToken;

    @Column(name = "TWITCH_REFRESH_TOKEN", length = 50)
    private String TwitchRefreshToken;
}
