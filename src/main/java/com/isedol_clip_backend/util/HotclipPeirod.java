package com.isedol_clip_backend.util;

public enum HotclipPeirod {
    WEEK(10),
    MONTH(20),
    QUARTER(30);

    private final int storeCnt;

    HotclipPeirod(int storeCnt) {
        this.storeCnt = storeCnt;
    }

    public int getStoreCnt() { return storeCnt; }
}