package com.isedol_clip_backend.util;

public enum HotclipPeirod {
    WEEK(10),
    MONTH(20),
    QUARTER(30);

    private final int value;

    HotclipPeirod(int value) {
        this.value = value;
    }

    public int getValue() { return value; }
}