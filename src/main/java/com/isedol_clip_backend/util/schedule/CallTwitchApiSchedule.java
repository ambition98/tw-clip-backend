package com.isedol_clip_backend.util.schedule;

import com.isedol_clip_backend.exception.RequestException;
import com.isedol_clip_backend.util.CallTwitchAPI;
import com.isedol_clip_backend.util.HotclipPeirod;
import com.isedol_clip_backend.util.HotclipsStorage;
import com.isedol_clip_backend.util.TwitchJsonModelMapper;
import com.isedol_clip_backend.util.aop.CheckRunningTime;
import com.isedol_clip_backend.web.model.TwitchClip;
import com.isedol_clip_backend.web.model.request.ReqClipsDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

@EnableAsync
@Component
@Slf4j
@Getter
@RequiredArgsConstructor
public class CallTwitchApiSchedule {

    private final CallTwitchAPI callTwitchAPI;
    private final HotclipsStorage hotclipsStorage;
    private static final String[] BROADCASTER_ID
            = new String[]{"195641865", "707328484", "203667951", "169700336", "237570548" ,"702754423"};
    private static final int FIRST = 4;
    public static final int WEEK_SIZE = 10;
    public static final int MONTH_SIZE = 20;
    public static final int QUARTER_SIZE = 30;

    @CheckRunningTime
    @Scheduled(cron = "0 0 * * * *")
    public void setNewHotclips() throws InterruptedException {
        log.info("[ Scheduled ]: setNewHotclips");
        setHotclips(HotclipPeirod.WEEK);
        setHotclips(HotclipPeirod.MONTH);
        setHotclips(HotclipPeirod.QUARTER);

//        ArrayList<TwitchClip[]> list = hotclipsStorage.getWeekHotclips();
//        log.info("total len: {}", list.size());
//        int idx = 0;
//        for(TwitchClip[] clips : list) {
//            log.info("--- {} ---", idx++);
//            log.info("len: {}", clips.length);
//            for(TwitchClip clip : clips) {
//                log.info(clip.toString());
//            }
//            log.info("-----------");
//        }
//
//        log.info("==========end=============");
//        setHotclips(HotclipPeirod.MONTH);
//
//        list = hotclipsStorage.getMonthHotclips();
//        log.info("total len: {}", list.size());
//        idx = 0;
//        for(TwitchClip[] clips : list) {
//            log.info("--- {} ---", idx++);
//            log.info("len: {}", clips.length);
//            for(TwitchClip clip : clips) {
//                log.info(clip.toString());
//            }
//            log.info("-----------");
//        }
//
//        log.info("==========end=============");
//        setHotclips(HotclipPeirod.QUARTER);
//
//        list = hotclipsStorage.getQuarterHotclips();
//        log.info("total len: {}", list.size());
//        idx = 0;
//        for(TwitchClip[] clips : list) {
//            log.info("--- {} ---", idx++);
//            log.info("len: {}", clips.length);
//            for(TwitchClip clip : clips) {
//                log.info(clip.toString());
//            }
//            log.info("-----------");
//        }
    }

    private void setHotclips(HotclipPeirod period) throws InterruptedException {
        ArrayList<TwitchClip[]> list = new ArrayList<>(period.getValue());
        String[] cursor = new String[6];
        String startedAt = getStartedAt(period);

        for(int i=0; i<period.getValue(); i++) {
            int idx = 0;
            TwitchClip[] clips = new TwitchClip[FIRST * BROADCASTER_ID.length];

            for(int j=0; j<BROADCASTER_ID.length; j++) {
                JSONObject jsonObject;
                ReqClipsDto reqClipsDto = new ReqClipsDto(BROADCASTER_ID[j], cursor[j],
                        FIRST, startedAt, DateSchedule.NOW);

                log.info("dto: {}", reqClipsDto);

                try {
                    jsonObject = callTwitchAPI.requestClips(reqClipsDto);
                } catch (IOException | RequestException | ParseException e) {
                    throw new RuntimeException(e);
                }

                cursor[j] = getCursor(jsonObject);
                TwitchClip[] tempClips;
                try {
                    tempClips = TwitchJsonModelMapper.clipMapping(jsonObject);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                for(TwitchClip clip : tempClips)
                    clips[idx++] = clip;
                Thread.sleep(100);
            }

            Arrays.sort(clips, getComparator());
            list.add(clips);
            if(hasNextData(cursor))
                break;
        }

        hotclipsStorage.setHotclips(period, list);
    }

    private boolean hasNextData(String[] cursor) {
        for(String s : cursor) {
            if(s != null)
                return true;
        }
        return false;
    }

    private String getCursor(JSONObject jsonObject) {
        try {
            return jsonObject.getString("pagination");
        } catch (JSONException e) {
            return null;
        }
    }

    private String getStartedAt(HotclipPeirod period) {
        switch (period) {
            case WEEK:
                return DateSchedule.WEEK_AGO;
            case MONTH:
                return DateSchedule.MONTH_AGO;
            case QUARTER:
                return DateSchedule.QUARTER_AGO;
        }

        return null;
    }

    private Comparator<TwitchClip> getComparator() {
        return (o1, o2) -> o2.getViewCount() - o1.getViewCount();
    }
}
