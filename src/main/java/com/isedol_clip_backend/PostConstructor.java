package com.isedol_clip_backend;

import com.isedol_clip_backend.util.schedule.CallTwitchApiSchedule;
import com.isedol_clip_backend.util.schedule.DateSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class PostConstructor {

    private final DateSchedule dateSchedule;
    private final CallTwitchApiSchedule callTwitchApiSchedule;

    @PostConstruct
    public void init() throws InterruptedException {
        dateSchedule.setDate();
        dateSchedule.setNow();

        callTwitchApiSchedule.setNewHotclips();
    }
}
