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
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final TwitchClipUtil twitchClipUtil;

    @GetMapping("/all")
    public ResponseEntity<CommonResponse> getFavorites() throws NoExistedDataException,
            ApiRequestException, IOException, ParseException {

        List<FavoriteEntity> entityList = favoriteService.getAllByAccountId(getAccountId());
        List<TwitchClip> clips = twitchClipUtil.getClipList(entityList);

        return MakeResp.make(HttpStatus.OK, "Success", clips);
    }

    @GetMapping("/exists")
    public ResponseEntity<CommonResponse> isExistedFavorite(final String clipId)
            throws NoExistedDataException {
        Boolean exists = favoriteService.exists(getAccountId(), clipId);
        return MakeResp.make(HttpStatus.OK, "Success", exists);
    }

    @PostMapping("/{clipId}")
    public ResponseEntity<CommonResponse> postFavorite(@PathVariable final String clipId) throws NoExistedDataException,
            AlreadyExistedDataException {
        Favorite favoriteDto = favoriteService.save(getAccountId(), clipId);
        return MakeResp.make(HttpStatus.OK, "Success", favoriteDto);
    }

    @DeleteMapping("/{clipId}")
    public ResponseEntity<CommonResponse> deleteFavorite(@PathVariable final String clipId) throws NoExistedDataException {
        favoriteService.delete(getAccountId(), clipId);
        return MakeResp.make(HttpStatus.OK, "Success");
    }

    @DeleteMapping("")
    public ResponseEntity<CommonResponse> deleteFavorites(@RequestBody final String body) throws NoExistedDataException {
        JSONArray jsonArray = new JSONObject(body).getJSONArray("clipsId");
        List<String> clipsId = new ArrayList<>(jsonArray.length());
        for (int i=0; i<jsonArray.length(); i++) {
            clipsId.add(jsonArray.getString(i));
        }

        int cntDeleted = favoriteService.deleteList(getAccountId(), clipsId);

        return MakeResp.make(HttpStatus.OK, "Success", cntDeleted);
    }

    private long getAccountId() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        return Long.parseLong(id);
    }
}
