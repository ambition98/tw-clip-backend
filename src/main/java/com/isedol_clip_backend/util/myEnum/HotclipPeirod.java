package com.isedol_clip_backend.util.myEnum;

public enum HotclipPeirod {
    DAY(10),
    WEEK(20),
    MONTH(30);

    private final int storeCnt;

    HotclipPeirod(int storeCnt) {
        this.storeCnt = storeCnt;
    }

    public int getStoreCnt() { return storeCnt; }
}