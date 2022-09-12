package com.isedol_clip_backend.util;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Convert yyyy-MM-dd to RFC3339 format or RFC3339 to yyyy-MM-dd hh:mm:ss
 * Apply time defference. (RFC3339 format is UTC, yyyy-MM-dd format is KST)
 */
@Slf4j
public class ConvertDateFormat {
    private static final long TIME_DIFFERENCE = 32_400_000; // UTC와 KST의 시차, 9시간
    private static final long ONE_DAY = 86_400_000;
    private static final ThreadLocal<SimpleDateFormat> rfcFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        }

        @Override
        public SimpleDateFormat get() {
            return super.get();
        }
    };

    private static final ThreadLocal<SimpleDateFormat> generalFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }

        @Override
        public SimpleDateFormat get() {
            return super.get();
        }
    };

    // For request TwitchAPI
    // yyyy-MM-dd -> yyyy-MM-dd'T'HH:mm:ss'Z'
    // KST -> UTC
    public static String generalToRfc(String time, convertType type) throws ParseException {
        Date d = generalFormat.get().parse(time);

        if(type == convertType.ENDED_AT) {
            d.setTime(d.getTime() + ONE_DAY);
        }

        d.setTime(d.getTime() - TIME_DIFFERENCE);

        return rfcFormat.get().format(d);
    }

    // For response Client
    // yyyy-MM-dd'T'HH:mm:ss'Z' -> yyyy-MM-dd
    // UTC -> KST
    public static String rfcToGeneral(String time) throws ParseException {
        Date d = rfcFormat.get().parse(time);
        d.setTime(d.getTime() + TIME_DIFFERENCE);

        return generalFormat.get().format(d);
    }

    public enum convertType {
        START_AT,
        ENDED_AT
    }
}
