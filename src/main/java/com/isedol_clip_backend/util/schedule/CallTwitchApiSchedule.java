package com.isedol_clip_backend.util.schedule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.isedol_clip_backend.exception.RequestException;
import com.isedol_clip_backend.util.CallTwitchAPI;
import com.isedol_clip_backend.util.HotclipPeirod;
import com.isedol_clip_backend.util.TwitchMapper;
import com.isedol_clip_backend.util.TwitchStorage;
import com.isedol_clip_backend.util.aop.CheckRunningTime;
import com.isedol_clip_backend.web.model.TwitchClip;
import com.isedol_clip_backend.web.model.TwitchUser;
import com.isedol_clip_backend.web.model.request.ReqClipsDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

@EnableAsync
@Component
@Slf4j
@Getter
@RequiredArgsConstructor
public class CallTwitchApiSchedule {

    private final CallTwitchAPI callTwitchAPI;
    private final TwitchStorage twitchStorage;
    private final TwitchMapper twitchMapper;
    private static final long[] BROADCASTER_ID
            = new long[]{195641865, 707328484, 203667951, 169700336, 237570548 ,702754423};
    private static final int FIRST = 4;
    public static final int WEEK_SIZE = 10;
    public static final int MONTH_SIZE = 20;
    public static final int QUARTER_SIZE = 30;

    @Async
    @Scheduled(cron = "0 0 * * * *")
    public void setIsedolInfo() throws IOException, RequestException {
        HashMap<Long, TwitchUser> isedolInfo = new HashMap<>(BROADCASTER_ID.length);
        JSONArray jsonArray = callTwitchAPI.requestUser(BROADCASTER_ID, null);
        TwitchUser[] users = twitchMapper.mappingUsers(jsonArray);
        for(TwitchUser user : users) {
            isedolInfo.put(user.getId(), user);
        }

        twitchStorage.setIsedolInfo(isedolInfo);
    }

    @CheckRunningTime
    @Async
    @Scheduled(cron = "0 0 * * * *")
    public void setHotclips() throws InterruptedException, JsonProcessingException {
        log.info("[ Scheduled ]: setNewHotclips");
        requestHotclips(HotclipPeirod.WEEK);
        requestHotclips(HotclipPeirod.MONTH);
        requestHotclips(HotclipPeirod.QUARTER);
    }

    private void requestHotclips(HotclipPeirod period) throws InterruptedException, JsonProcessingException {
        ArrayList<TwitchClip[]> list = new ArrayList<>(period.getValue());
        String[] cursor = new String[6];
        String startedAt = getStartedAt(period);

        for(int i=0; i<period.getValue(); i++) {
            int idx = 0;
            ArrayList<TwitchClip> clips = new ArrayList<>();

            for(int j=0; j<BROADCASTER_ID.length; j++) {
                JSONObject jsonObject;
                ReqClipsDto reqClipsDto = new ReqClipsDto(BROADCASTER_ID[j], cursor[j],
                        FIRST, startedAt, DateSchedule.NOW);

//                log.info("dto: {}", reqClipsDto);

                try {
                    jsonObject = callTwitchAPI.requestClips(reqClipsDto);
                } catch (IOException | RequestException | ParseException e) {
                    throw new RuntimeException(e);
                }

                cursor[j] = getCursor(jsonObject);
                TwitchClip[] tempClips;
                tempClips = twitchMapper.mappingClips(jsonObject);
                log.info("clips: {}", Arrays.toString(tempClips));

                clips.addAll(Arrays.asList(tempClips));
                Thread.sleep(100);
            }

            clips.sort(getComparator());
            list.add(clips.toArray(new TwitchClip[0]));
            if(!hasNextData(cursor))
                break;
        }

        twitchStorage.setHotclips(period, list);
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
            return jsonObject.getJSONObject("pagination").getString("cursor");
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
