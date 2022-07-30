package com.isedol_clip_backend.util;

import com.isedol_clip_backend.web.model.TwitchClip;
import com.isedol_clip_backend.web.model.TwitchUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
@Slf4j
public class TwitchStorage {
    private HashMap<Long, TwitchUser> isedolInfo;
    private ArrayList<TwitchClip[]> weekHotclips;
    private ArrayList<TwitchClip[]> monthHotclips;
    private ArrayList<TwitchClip[]> quarterHotclips;

    public TwitchUser getIsedolInfo(long id) {
        return isedolInfo.get(id);
    }

    public HashMap<Long, TwitchUser> getIsedolInfo() {
        return isedolInfo;
    }

    public boolean isIsedol(long id) {
        return isedolInfo.containsKey(id);
    }

    public void setIsedolInfo(HashMap<Long, TwitchUser> isedolInfo) {
        this.isedolInfo = isedolInfo;
    }

    public void setHotclips(HotclipPeirod period
            , ArrayList<TwitchClip[]> clips) {
        switch (period) {
            case WEEK:
                weekHotclips = clips;
                break;
            case MONTH:
                monthHotclips = clips;
                break;
            case QUARTER:
                quarterHotclips = clips;
        }
    }

    public TwitchClip[] getHotclips(HotclipPeirod period, int page) {
        try {
            switch (period) {
                case WEEK:
                    return weekHotclips.get(page-1);
                case MONTH:
                    return monthHotclips.get(page-1);
                case QUARTER:
                    return quarterHotclips.get(page-1);
            }
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

        return null;
    }
}
