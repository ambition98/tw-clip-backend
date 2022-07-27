package com.isedol_clip_backend.util.schedule;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@EnableAsync
@Component
@Slf4j
@Getter
public class DateSchedule {
    @Getter(AccessLevel.NONE)
//    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
    public static String NOW;
    public static String WEEK_AGO;
    public static String MONTH_AGO;
    public static String QUARTER_AGO;

    @Async
    @Scheduled(cron = "0 0 * * * *")
    public void setDate() {
        log.info("setDate cron");
        Calendar week = Calendar.getInstance();
        Calendar month = Calendar.getInstance();
        Calendar quarter = Calendar.getInstance();

        week.add(Calendar.DATE, -7);
        month.add(Calendar.MONTH, -1);
        quarter.add(Calendar.MONTH, -3);

        week.add(Calendar.HOUR, -9);
        month.add(Calendar.HOUR, -9);
        quarter.add(Calendar.HOUR, -9);

        WEEK_AGO = SDF.format(week.getTime());
        MONTH_AGO = SDF.format(month.getTime());
        QUARTER_AGO = SDF.format(quarter.getTime());

        log.info("weekAgo: {}", WEEK_AGO);
        log.info("monthAgo: {}", MONTH_AGO);
        log.info("quarterAgo: {}", QUARTER_AGO);
    }

    @Async
    @Scheduled(cron = "0 0 0 * * *")
    public void setNow() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 2);
        NOW = SDF.format(c.getTime());
        log.info("now: {}", NOW);
    }
}
