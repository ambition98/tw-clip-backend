package com.isedol_clip_backend;

import com.isedol_clip_backend.auth.JwtTokenProvider;
import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.util.LoadSecret;
import com.isedol_clip_backend.util.schedule.CallTwitchApiSchedule;
import com.isedol_clip_backend.util.schedule.DateSchedule;
import com.isedol_clip_backend.web.controller.TwitchEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostConstructor {
    private final JwtTokenProvider jwtTokenProvider;
    private final DateSchedule dateSchedule;
    private final CallTwitchApiSchedule callTwitchApiSchedule;
    private final LoadSecret loadSecret;

    private final TwitchEventListener twitchEventListener;
    @PostConstruct
    public void init() throws ParseException {
        loadSecret.load();
        jwtTokenProvider.setKey(loadSecret.getJwtSecret());
        dateSchedule.setDate();

        try {
            callTwitchApiSchedule.assignTwitchAccessToken();
            callTwitchApiSchedule.setIsedolInfo();
            callTwitchApiSchedule.setHotclips();
//            twitchEventListener.trysub();
        } catch (NoExistedDataException e) {
            // Do Nothing
        } catch (Exception e) {
            log.error("Fail to initialization");
            e.printStackTrace();
        }

    }
}
