package com.isedol_clip_backend.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

/**
 * Convert yyyy-MM-dd to RFC3339 format or RFC3339 to yyyy-MM-dd hh:mm:ss
 * Apply time defference. (RFC3339 format is UTC, yyyy-MM-dd format is KST)
 */
public class ConvertCalender {
    private static final SimpleDateFormat SDF_1 = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat SDF_2 = new SimpleDateFormat("HH:mm:ss");
    private static final long TIME_DIFFERENCE = 32_400_000; //9시간

    // For request TwitchAPI
    public static String generalFormatToRfc3339(String time, convertType type) {
        int[] ymd = getYMD(time);
        int year = ymd[0];
        int month = ymd[1];
        int date = ymd[2];

        if(type == convertType.ENDED_AT)
            date++;

        Date d = new Date(new GregorianCalendar(year, month, date).getTimeInMillis() - TIME_DIFFERENCE);

        return SDF_1.format(d) + "T" + SDF_2.format(d) + "Z";
    }

    // For response Client
    public static String rfc3339ToGeneralFormat(String time) {
        StringTokenizer st = new StringTokenizer(time, "T");
        String stringYmd = st.nextToken();
        String stringHms = st.nextToken();

        int[] ymd = getYMD(stringYmd);
        int year = ymd[0];
        int month = ymd[1];
        int date = ymd[2];

        st = new StringTokenizer(stringHms, ":");
        int hour = deleteZero(st.nextToken());
        int minute = deleteZero(st.nextToken());
        int second = deleteZero(st.nextToken().substring(0, 2));

        return year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second;
    }

    private static int[] getYMD(String time) {
        StringTokenizer st = new StringTokenizer(time, "-");
        int year = Integer.parseInt(st.nextToken());

        String sMonth = st.nextToken();
        String sDate = st.nextToken().substring(0, 2);

        int month = deleteZero(sMonth) - 1;
        int date = deleteZero(sDate);

        return new int[] {year, month, date};
    }

    private static int deleteZero(String s) {
        return Integer.parseInt(s.charAt(0) == '0' ? Character.toString(s.charAt(1)) : s);
    }

    public enum convertType {
        START_AT,
        ENDED_AT
    }
}
