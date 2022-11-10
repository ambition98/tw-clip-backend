package com.isedol_clip_backend.util.schedule;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@EnableAsync
@Component
@Slf4j
@Getter
public class DateSchedule {
    @Getter(AccessLevel.NONE)
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    public static String NOW;
    public static String WEEK_AGO;
    public static String MONTH_AGO;
    public static String DAY_AGO;

    // UTC 기준
    @Scheduled(cron = "0 0 * * * *")
    public void setDate() throws ParseException {
        Calendar day = Calendar.getInstance();
        Calendar week = Calendar.getInstance();
        Calendar month = Calendar.getInstance();

        day.add(Calendar.DATE, -1);
        week.add(Calendar.DATE, -7);
        month.add(Calendar.MONTH, -1);

        day.add(Calendar.HOUR, -9);
        week.add(Calendar.HOUR, -9);
        month.add(Calendar.HOUR, -9);

        DAY_AGO = SDF.format(day.getTime());
        WEEK_AGO = SDF.format(week.getTime());
        MONTH_AGO = SDF.format(month.getTime());

        log.info("dayAgo: {}", DAY_AGO);
        log.info("weekAgo: {}", WEEK_AGO);
        log.info("monthAgo: {}", MONTH_AGO);
    }
}
