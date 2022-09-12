package com.isedol_clip_backend.web.controller.user;

import com.isedol_clip_backend.exception.AlreadyExistedDataException;
import com.isedol_clip_backend.exception.ApiRequestException;
import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.util.MakeResp;
import com.isedol_clip_backend.util.TwitchClipUtil;
import com.isedol_clip_backend.web.entity.FavoriteEntity;
import com.isedol_clip_backend.web.model.Favorite;
import com.isedol_clip_backend.web.model.TwitchClip;
import com.isedol_clip_backend.web.model.response.CommonResponse;
import com.isedol_clip_backend.web.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final TwitchClipUtil twitchClipUtil;

    @GetMapping("/favorites")
    public ResponseEntity<CommonResponse> getFavorites() throws NoExistedDataException,
            ApiRequestException, IOException, ParseException {

        List<FavoriteEntity> entityList = favoriteService.getAllByAccountId(getAccountId());
        List<TwitchClip> clips = twitchClipUtil.getClipList(entityList);

        return MakeResp.make(HttpStatus.OK, "Success", clips);
    }

    @PostMapping("/favorite")
    public ResponseEntity<CommonResponse> postFavorite(@RequestBody final String body) throws NoExistedDataException,
            AlreadyExistedDataException {
        JSONObject jsonObject = new JSONObject(body);
        String clipId = jsonObject.getString("clipId");
        Favorite favoriteDto = favoriteService.save(getAccountId(), clipId);

        return MakeResp.make(HttpStatus.OK, "Success", favoriteDto);
    }

    @DeleteMapping("/favorite/{clipId}")
    public ResponseEntity<CommonResponse> deleteFavorite(@PathVariable final String clipId) throws NoExistedDataException {
        favoriteService.delete(getAccountId(), clipId);
        return MakeResp.make(HttpStatus.OK, "Success");
    }

    @GetMapping("/favorite/exists")
    public ResponseEntity<CommonResponse> isExistedFavorite(final String clipId)
            throws NoExistedDataException {
        Boolean exists = favoriteService.exists(getAccountId(), clipId);
        return MakeResp.make(HttpStatus.OK, "Success", exists);
    }

    private long getAccountId() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        return Long.parseLong(id);
    }

//    private List<TwitchClip> getClipList(List<FavoriteEntity> entityList) throws ApiRequestException, IOException, ParseException {
//        List<TwitchClip> clips = new ArrayList<>(entityList.size());
//        List<String> temp = new ArrayList<>(100);
//        for(FavoriteEntity entity : entityList) {
//            temp.add(entity.getClipId());
//            if(temp.size() == 100) {
//                String[] idArr = temp.toArray(new String[0]);
//                JSONObject jsonObject = callTwitchApi.requestClipsById(idArr);
//                clips.addAll(twitchMapper.mappingClips(jsonObject));
//                temp.clear();
//            }
//        }
//
//        if(entityList.size() % 100 != 0) {
//            temp.clear();
//            for(int i=entityList.size() / 100 * 100; i<entityList.size(); i++) {
//                temp.add(entityList.get(i).getClipId());
//            }
//            String[] idArr = temp.toArray(new String[0]);
//            JSONObject jsonObject = callTwitchApi.requestClipsById(idArr);
//            clips.addAll(twitchMapper.mappingClips(jsonObject));
//        }
//
//        return clips;
//    }
}
