package com.sniper.game.wordgame.controller;

import com.sniper.game.wordgame.dto.DailyClearRequest;
import com.sniper.game.wordgame.dto.DailyClearResponse;
import com.sniper.game.wordgame.dto.DailyRankResponse;
import com.sniper.game.wordgame.dto.DailyHelpResponse;
import com.sniper.game.wordgame.dto.DailyStatusResponse;
import com.sniper.game.wordgame.dto.LoginRequest;
import com.sniper.game.wordgame.dto.LoginResponse;
import com.sniper.game.wordgame.dto.GameConfigResponse;
import com.sniper.game.wordgame.dto.ProgressRequest;
import com.sniper.game.wordgame.dto.RankRequest;
import com.sniper.game.wordgame.dto.RankResponse;
import com.sniper.game.wordgame.dto.ProfileRequest;
import com.sniper.game.wordgame.dto.RegionSaveRequest;
import com.sniper.game.wordgame.dto.ResourceUploadResponse;
import com.sniper.game.wordgame.dto.ResourceItem;
import com.sniper.game.wordgame.dto.ShareConsumeRequest;
import com.sniper.game.wordgame.dto.ShareConsumeResponse;
import com.sniper.game.wordgame.dto.SignInRewardItem;
import com.sniper.game.wordgame.service.RegionService;
import com.sniper.game.wordgame.service.ResourceService;
import com.sniper.game.wordgame.service.UserService;
import com.sniper.game.wordgame.util.UserContext;
import com.sniper.game.wordgame.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final UserService userService;
    private final RegionService regionService;
    private final ResourceService resourceService;

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

    /** 每日挑战状态：今天是否已通关（进每日挑战时读） */
    @PostMapping("/daily/status")
    public Result<DailyStatusResponse> dailyStatus(@RequestBody ProgressRequest request) {
        return Result.success(userService.getDailyStatus(UserContext.getCurrentUserId(), request.getGameType()));
    }

    /**
     * 每日挑战通关上报（过完第 2 关调）：一人一天一行，重复挑战更快则刷新起止时间。
     * 返回本次与今日最快耗时，通关页直接用，不必再多请求一次。
     */
    @PostMapping("/daily/clear")
    public Result<DailyClearResponse> dailyClear(@RequestBody DailyClearRequest request) {
        return Result.success(
                userService.saveDailyClear(UserContext.getCurrentUserId(), request.getGameType(), request.getStartAt(), request.getEndAt()));
    }

    /** 每日挑战省份榜：当天各省通关人数排行（DENSE_RANK 并列） */
    @PostMapping("/daily/rank")
    public Result<DailyRankResponse> dailyRank(@Valid @RequestBody RankRequest request) {
        return Result.success(userService.getDailyRankList(UserContext.getCurrentUserId(), request.getGameType()));
    }

    /** 求助好友状态：指定模式今日已用次数/上限/剩余（上限读 help_max 配置） */
    @PostMapping("/daily-help/status")
    public Result<DailyHelpResponse> dailyHelpStatus(@RequestBody ProgressRequest request) {
        Long userId = UserContext.getCurrentUserId();
        String mode = com.sniper.game.wordgame.service.UserService.normalizeHelpMode(request.getMode());
        return Result.success(new DailyHelpResponse(
                userService.getDailyHelpUsed(userId, mode), userService.getHelpMax(mode)));
    }

    /** 求助好友使用：次数+1（达上限不再自增），返回最新次数 */
    @PostMapping("/daily-help/use")
    public Result<DailyHelpResponse> useDailyHelp(@RequestBody ProgressRequest request) {
        Long userId = UserContext.getCurrentUserId();
        String mode = com.sniper.game.wordgame.service.UserService.normalizeHelpMode(request.getMode());
        return Result.success(new DailyHelpResponse(
                userService.useDailyHelp(userId, mode), userService.getHelpMax(mode)));
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

    /** 公共上传：图片传 OSS，只返回 CDN 地址（不落库） */
    @PostMapping("/resource/upload")
    public Result<ResourceUploadResponse> uploadResource(@RequestParam("file") MultipartFile file) {
        return Result.success(resourceService.upload(file));
    }

    /** 七日签到奖励配置：7 天列表（含奖励图 URL），前端弹窗渲染用 */
    @PostMapping("/signin/config")
    public Result<List<SignInRewardItem>> signInConfig() {
        return Result.success(resourceService.getSignInRewards());
    }

    /** 资源查询：所有登记了类型编码的资源明细（前端按 resourceCode 组 Map，value=整条数据） */
    @PostMapping("/resources")
    public Result<List<ResourceItem>> resources() {
        return Result.success(userService.getResourceList());
    }
}
