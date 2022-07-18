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
        int reqCnt = idLen + nameLen;

        return reqCnt > 0 && reqCnt <= 100;
    }
}
