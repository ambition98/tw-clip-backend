package com.isedol_clip_backend.web.controller;

import com.isedol_clip_backend.util.HotclipPeirod;
import com.isedol_clip_backend.util.MakeResp;
import com.isedol_clip_backend.util.TwitchStorage;
import com.isedol_clip_backend.web.model.TwitchClip;
import com.isedol_clip_backend.web.model.TwitchUser;
import com.isedol_clip_backend.web.model.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;

@Slf4j
@Controller
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageController {

    private final TwitchStorage twitchStorage;

    @GetMapping("/hotclips")
    public ResponseEntity<CommonResponse> getHotclips(final String period, final int page) {
        TwitchClip[] clipsDto = null;
//        log.info("period: {}, page: {}", period, page);
        switch (period) {
            case "week":
                clipsDto = twitchStorage.getHotclips(HotclipPeirod.WEEK, page);
                break;
            case "month":
                clipsDto = twitchStorage.getHotclips(HotclipPeirod.MONTH, page);
                break;
            case "quarter":
                clipsDto = twitchStorage.getHotclips(HotclipPeirod.QUARTER, page);
        }

//        log.info("dto: {}", Arrays.toString(clipsDto));
        return MakeResp.make(HttpStatus.OK, "Success", clipsDto);
    }

    @GetMapping("/isedol")
    public ResponseEntity<CommonResponse> getIsedolInfo() {
        HashMap<Long, TwitchUser> isedolInfo = twitchStorage.getIsedolInfo();
        return MakeResp.make(HttpStatus.OK, "Success", isedolInfo);
    }
}
