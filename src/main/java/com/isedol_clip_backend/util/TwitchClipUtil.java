package com.isedol_clip_backend.util;

import com.isedol_clip_backend.exception.ApiRequestException;
import com.isedol_clip_backend.web.entity.ClipsEntity;
import com.isedol_clip_backend.web.model.TwitchClip;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TwitchClipUtil {

    private final CallTwitchAPI callTwitchApi;
    private final TwitchMapper twitchMapper;

    public List<TwitchClip> getClipList(List<? extends ClipsEntity> entityList) throws ApiRequestException, IOException,
            ParseException {
        List<TwitchClip> clips = new ArrayList<>(entityList.size());
        List<String> temp = new ArrayList<>(100);
        for(ClipsEntity entity : entityList) {
            temp.add(entity.getClipId());
            if(temp.size() == 100) {
                String[] idArr = temp.toArray(new String[0]);
                JSONObject jsonObject = callTwitchApi.requestClipsById(idArr);
                clips.addAll(twitchMapper.mappingClips(jsonObject));
                temp.clear();
            }
        }

        if(entityList.size() % 100 != 0) {
            temp.clear();
            for(int i=entityList.size() / 100 * 100; i<entityList.size(); i++) {
                temp.add(entityList.get(i).getClipId());
            }
            String[] idArr = temp.toArray(new String[0]);
            JSONObject jsonObject = callTwitchApi.requestClipsById(idArr);
            clips.addAll(twitchMapper.mappingClips(jsonObject));
        }

        setRegdate(entityList, clips);

        return clips;
    }

    private void setRegdate(List<? extends ClipsEntity> entityList, List<TwitchClip> clips) {
        sortEntityList(entityList);
        sortTwitchClip(clips);

        int targetIdx = 0;
        for (ClipsEntity src : entityList) {
            TwitchClip dst = clips.get(targetIdx);
            if (src.getClipId().equals(dst.getId())) {
                dst.setRegdate(src.getCreatedAt());
                if (++targetIdx >= clips.size()) {
                    break;
                }
            }
        }
    }

    private void sortEntityList(List<? extends ClipsEntity> entityList) {
        entityList.sort(Comparator.comparing(ClipsEntity::getClipId));
    }

    private void sortTwitchClip(List<TwitchClip> entityList) {
        entityList.sort(Comparator.comparing(TwitchClip::getId));
    }
}
