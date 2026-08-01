package com.sniper.game.wordgame.controller;

import com.sniper.game.wordgame.dto.LoginRequest;
import com.sniper.game.wordgame.dto.LoginResponse;
import com.sniper.game.wordgame.dto.GameConfigResponse;
import com.sniper.game.wordgame.dto.ProgressRequest;
import com.sniper.game.wordgame.dto.RankRequest;
import com.sniper.game.wordgame.dto.RankResponse;
import com.sniper.game.wordgame.dto.ProfileRequest;
import com.sniper.game.wordgame.dto.RegionSaveRequest;
import com.sniper.game.wordgame.dto.ShareConsumeRequest;
import com.sniper.game.wordgame.dto.ShareConsumeResponse;
import com.sniper.game.wordgame.service.RegionService;
import com.sniper.game.wordgame.service.UserService;
import com.sniper.game.wordgame.util.UserContext;
import com.sniper.game.wordgame.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final UserService userService;
    private final RegionService regionService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request.getCode(), request.getGameType(), request.getSource()));
    }

    @PostMapping("/config")
    public Result<GameConfigResponse> getGameConfig(@RequestBody ProgressRequest request) {
        return Result.success(userService.getGameConfig(request.getGameType()));
    }

    @PostMapping("/progress")
    public Result<Void> saveProgress(@Valid @RequestBody ProgressRequest request) {
        userService.saveProgress(UserContext.getCurrentUserId(), request.getGameType(), request.getLevelNum());
        return Result.success();
    }

    @PostMapping("/rank")
    public Result<RankResponse> rank(@Valid @RequestBody RankRequest request) {
        Long userId = UserContext.getCurrentUserId();
        return Result.success(userService.getRankList(userId, request.getGameType()));
    }

    @PostMapping("/profile")
    public Result<Void> updateProfile(@Valid @RequestBody ProfileRequest request) {
        Long userId = UserContext.getCurrentUserId();
        userService.updateProfile(userId, request.getNickname(), request.getAvatarUrl());
        return Result.success();
    }

    @PostMapping("/share/consume")
    public Result<ShareConsumeResponse> consumeShareCount(@RequestBody ShareConsumeRequest request) {
        Long userId = UserContext.getCurrentUserId();
        ShareConsumeResponse response = userService.consumeShareCount(userId, request.getGameType());
        return Result.success(response);
    }

    /** 地区字典列表（缓存优先）：选地区弹窗拉起时调 */
    @PostMapping("/region/list")
    public Result<List<RegionService.RegionItem>> regionList() {
        return Result.success(regionService.listRegions());
    }

    /** 保存用户选的地区（存 region.id） */
    @PostMapping("/region")
    public Result<Void> saveRegion(@Valid @RequestBody RegionSaveRequest request) {
        Long userId = UserContext.getCurrentUserId();
        regionService.saveUserRegion(userId, request.getRegionId());
        return Result.success();
    }
}
