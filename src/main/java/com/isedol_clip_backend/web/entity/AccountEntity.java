package com.isedol_clip_backend.web.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@Entity
@Table(name = "ACCOUNT")
public class AccountEntity extends GeneralEntity {
    @Id
    @Column(name = "ID")
    private long id;

    @Column(name = "TWITCH_ACCESS_TOKEN", length = 100)
    private String TwitchAccessToken;

    @Column(name = "TWITCH_REFRESH_TOKEN", length = 100)
    private String TwitchRefreshToken;

    @Column(name = "REFRESH_TOKEN", length = 200)
    private String refreshToken;
}
