package com.isedol_clip_backend;

import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.util.schedule.CallTwitchApiSchedule;
import com.isedol_clip_backend.util.schedule.DateSchedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostConstructor {

    private final DateSchedule dateSchedule;
    private final CallTwitchApiSchedule callTwitchApiSchedule;

    @PostConstruct
    public void init() {
        dateSchedule.setDate();
        dateSchedule.setNow();

        try {
            callTwitchApiSchedule.assignTwitchAccessToken();
            callTwitchApiSchedule.setIsedolInfo();
            callTwitchApiSchedule.setHotclips();
        } catch (NoExistedDataException e) {
            // Do Nothing
        } catch (Exception e) {
            log.error("Fail to initialization");
            e.printStackTrace();
        }

    }
}
