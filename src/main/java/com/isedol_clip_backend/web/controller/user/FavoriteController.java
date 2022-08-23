package com.isedol_clip_backend.web.controller.user;

import com.isedol_clip_backend.exception.AlreadyExistedDataException;
import com.isedol_clip_backend.exception.ApiRequestException;
import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.util.CallTwitchAPI;
import com.isedol_clip_backend.util.MakeResp;
import com.isedol_clip_backend.util.TwitchMapper;
import com.isedol_clip_backend.web.model.Favorite;
import com.isedol_clip_backend.web.model.TwitchClip;
import com.isedol_clip_backend.web.model.response.CommonResponse;
import com.isedol_clip_backend.web.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.data.domain.PageRequest;
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

    private final CallTwitchAPI callTwitchApi;
    private final FavoriteService favoriteService;
    private final TwitchMapper twitchMapper;

    @GetMapping("/favorites")
    public ResponseEntity<CommonResponse> getFavorites(final int page) throws NoExistedDataException,
            ApiRequestException, IOException, ParseException {

        PageRequest pageRequest = PageRequest.of(page, 100);
        List<String> idList =  favoriteService.getByAccountId(getAccountId(), pageRequest);
        String[] idArr = idList.toArray(idList.toArray(new String[0]));

        JSONObject jsonObject = callTwitchApi.requestClipsById(idArr);
        TwitchClip[] clips = twitchMapper.mappingClips(jsonObject);

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
}
