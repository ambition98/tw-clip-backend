package com.isedol_clip_backend.util.schedule;

import com.isedol_clip_backend.exception.ApiRequestException;
import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.util.*;
import com.isedol_clip_backend.util.aop.CheckScheduled;
import com.isedol_clip_backend.util.myEnum.HotclipPeirod;
import com.isedol_clip_backend.web.model.TwitchClip;
import com.isedol_clip_backend.web.model.TwitchUser;
import com.isedol_clip_backend.web.model.request.ReqClipsDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;

@EnableAsync
@Component
@Slf4j
@Getter
@RequiredArgsConstructor
public class CallTwitchApiSchedule {
    @Value("${secret.path}")
    private String secretPath;
    private final LoadSecret loadSecret;
    private final CallTwitchAPI callTwitchAPI;
    private final TwitchStorage twitchStorage;
    private final TwitchMapper twitchMapper;
    private static final long[] BROADCASTER_ID
            = new long[]{195641865, 707328484, 203667951, 169700336, 237570548 ,702754423};
    private static final int FIRST = 4;

    @Async
    @CheckScheduled
    @Scheduled(cron = "0 0 * * * *")
    public void setIsedolInfo() throws IOException, ApiRequestException, NoExistedDataException {
        HashMap<Long, TwitchUser> isedolInfo = new HashMap<>(BROADCASTER_ID.length);
        JSONArray jsonArray = callTwitchAPI.requestUser(BROADCASTER_ID, null);
        TwitchUser[] users = twitchMapper.mappingUsers(jsonArray);
        for(TwitchUser user : users) {
            isedolInfo.put(user.getId(), user);
        }

        twitchStorage.setIsedolInfo(isedolInfo);
    }

    @CheckScheduled
    @Async
    @Scheduled(cron = "0 0 * * * *")
    public void setHotclips() throws InterruptedException, IOException, ParseException, NoExistedDataException, ApiRequestException {
        requestHotclips(HotclipPeirod.DAY);
        requestHotclips(HotclipPeirod.WEEK);
        requestHotclips(HotclipPeirod.MONTH);
    }

    @CheckScheduled
    @Scheduled(cron = "0 0 0 * * 1")
    public void assignTwitchAccessToken() throws ApiRequestException, IOException {
        JSONObject jsonObject = callTwitchAPI.requestAccessToken();
        String newAccessToken = jsonObject.getString("access_token");
        JSONObject result = new JSONObject();
        result.put("twitch_client_id", loadSecret.getTwitchClientId());
        result.put("twitch_secret", loadSecret.getTwitchSecret());
        result.put("twitch_access_token", newAccessToken);
        result.put("jwt_secret", loadSecret.getJwtSecret());
        byte[] data = result.toString().getBytes(StandardCharsets.UTF_8);

        FileUtil.putDataToFilePath(secretPath, data);
        loadSecret.load();
    }


    private void requestHotclips(HotclipPeirod period) throws InterruptedException, IOException, ParseException, ApiRequestException {
        ArrayList<TwitchClip[]> list = new ArrayList<>(period.getStoreCnt());
        String[] cursor = new String[6];
        String startedAt = getStartedAt(period);

        for(int i = 0; i<period.getStoreCnt(); i++) {
            ArrayList<TwitchClip> clips = new ArrayList<>();

            for(int j=0; j<BROADCASTER_ID.length; j++) {
                JSONObject jsonObject;
                ReqClipsDto reqClipsDto = new ReqClipsDto(BROADCASTER_ID[j], cursor[j],
                        FIRST, startedAt, DateSchedule.NOW);

                try {
                    jsonObject = callTwitchAPI.requestClips(reqClipsDto);
                } catch (NoExistedDataException e) {
                    log.warn("Throwed NoExistedDataException during request hotclips");
                    log.warn("Faild dto: {}", reqClipsDto);
                    continue;
                }

                cursor[j] = getCursor(jsonObject);
                List<TwitchClip> tempClips = twitchMapper.mappingClips(jsonObject);

                clips.addAll(tempClips);
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
            case DAY:
                return DateSchedule.DAY_AGO;
            case WEEK:
                return DateSchedule.WEEK_AGO;
            case MONTH:
                return DateSchedule.MONTH_AGO;
        }

        return null;
    }

    private Comparator<TwitchClip> getComparator() {
        return (o1, o2) -> o2.getViewCount() - o1.getViewCount();
    }
}
