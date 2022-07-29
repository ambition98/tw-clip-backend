package com.isedol_clip_backend;

import com.isedol_clip_backend.exception.RequestException;
import com.isedol_clip_backend.util.schedule.CallTwitchApiSchedule;
import com.isedol_clip_backend.util.schedule.DateSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class PostConstructor {

    private final DateSchedule dateSchedule;
    private final CallTwitchApiSchedule callTwitchApiSchedule;

    @PostConstruct
    public void init() throws InterruptedException, IOException, RequestException {
        dateSchedule.setDate();
        dateSchedule.setNow();

        callTwitchApiSchedule.setHotclips();
        callTwitchApiSchedule.setIsedolInfo();
    }
}
