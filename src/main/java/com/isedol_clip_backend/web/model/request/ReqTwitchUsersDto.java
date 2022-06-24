package com.isedol_clip_backend.web.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReqTwitchUsersDto {
    private int[] id;
    private String[] login;

    public boolean isValid() {
        int idLen = id == null ? 0 : id.length;
        int nameLen = login == null ? 0 : login.length;

        return idLen + nameLen <= 100;
    }
}
