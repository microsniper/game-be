package com.sniper.game.wordgame.controller;

import com.sniper.game.wordgame.annotation.ApiName;
import com.sniper.game.wordgame.dto.DailyClearRequest;
import com.sniper.game.wordgame.dto.FeedbackSubmitRequest;
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
import com.sniper.game.wordgame.dto.RewardDailyRequest;
import com.sniper.game.wordgame.dto.RewardEndlessRequest;
import com.sniper.game.wordgame.dto.RewardItem;
import com.sniper.game.wordgame.dto.CollectByCodesRequest;
import com.sniper.game.wordgame.dto.CollectByIdsRequest;
import com.sniper.game.wordgame.dto.ShopListRequest;
import com.sniper.game.wordgame.dto.ShopPageDto;
import com.sniper.game.wordgame.dto.CollectItemDto;
import com.sniper.game.wordgame.dto.BackpackItemDto;
import com.sniper.game.wordgame.dto.BackpackOwnRequest;
import com.sniper.game.wordgame.dto.BackpackSetCurrentRequest;
import com.sniper.game.wordgame.dto.MyStoragePageDto;
import com.sniper.game.wordgame.dto.MyStorageRequest;
import com.sniper.game.wordgame.service.BackpackService;
import com.sniper.game.wordgame.service.CollectService;
import com.sniper.game.wordgame.service.FeedbackService;
import com.sniper.game.wordgame.service.RegionService;
import com.sniper.game.wordgame.service.ResourceService;
import com.sniper.game.wordgame.service.RewardService;
import com.sniper.game.wordgame.service.ShopService;
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
    private final RewardService rewardService;
    private final ShopService shopService;
    private final FeedbackService feedbackService;
    private final CollectService collectService;
    private final BackpackService backpackService;

    @ApiName("登录接口")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request.getCode(), request.getGameType(), request.getSource()));
    }

    @ApiName("游戏配置接口")
    @PostMapping("/config")
    public Result<GameConfigResponse> getGameConfig(@RequestBody ProgressRequest request) {
        return Result.success(userService.getGameConfig(request.getGameType()));
    }

    @ApiName("保存进度接口")
    @PostMapping("/progress")
    public Result<Void> saveProgress(@Valid @RequestBody ProgressRequest request) {
        userService.saveProgress(UserContext.getCurrentUserId(), request.getGameType(), request.getLevelNum());
        return Result.success();
    }

    @ApiName("排行榜接口")
    @PostMapping("/rank")
    public Result<RankResponse> rank(@Valid @RequestBody RankRequest request) {
        Long userId = UserContext.getCurrentUserId();
        return Result.success(userService.getRankList(userId, request.getGameType()));
    }

    /**
     * 无限榜（展示专用，合并 endless_rank_mock 虚拟玩家）：排行榜页列表与「我的排名」都用这个接口，
     * myRank 已按合并榜算好位置。旧接口 /rank 保留纯真实数据，用户量上来后切回它即可。
     */
    @ApiName("无限榜展示接口")
    @PostMapping("/rank/config")
    public Result<RankResponse> rankConfig(@Valid @RequestBody RankRequest request) {
        return Result.success(userService.getRankDisplayList(UserContext.getCurrentUserId(), request.getGameType()));
    }

    /** 每日挑战状态：今天是否已通关（进每日挑战时读） */
    @ApiName("每日挑战状态接口")
    @PostMapping("/daily/status")
    public Result<DailyStatusResponse> dailyStatus(@RequestBody ProgressRequest request) {
        return Result.success(userService.getDailyStatus(UserContext.getCurrentUserId(), request.getGameType()));
    }

    /**
     * 每日挑战通关上报（过完第 2 关调）：一人一天一行，重复挑战更快则刷新起止时间。
     * 返回本次与今日最快耗时，通关页直接用，不必再多请求一次。
     */
    @ApiName("每日挑战通关上报接口")
    @PostMapping("/daily/clear")
    public Result<DailyClearResponse> dailyClear(@RequestBody DailyClearRequest request) {
        return Result.success(
                userService.saveDailyClear(UserContext.getCurrentUserId(), request.getGameType(), request.getStartAt(), request.getEndAt()));
    }

    /** 每日挑战省份榜：当天各省通关人数排行（DENSE_RANK 并列） */
    @ApiName("每日挑战省份榜接口")
    @PostMapping("/daily/rank")
    public Result<DailyRankResponse> dailyRank(@Valid @RequestBody RankRequest request) {
        return Result.success(userService.getDailyRankList(UserContext.getCurrentUserId(), request.getGameType()));
    }

    /**
     * 每日挑战省份榜（展示专用，叠加 daily_rank_mock 虚拟基数）：首页/排行榜页的省份列表展示用这个接口。
     * “我的排名”卡片仍应使用 /daily/rank 的真实数据，不要用这个接口的 myRank。
     */
    @ApiName("每日挑战省份榜展示接口")
    @PostMapping("/daily/rank/config")
    public Result<DailyRankResponse> dailyRankConfig(@Valid @RequestBody RankRequest request) {
        return Result.success(userService.getDailyRankDisplayList(UserContext.getCurrentUserId(), request.getGameType()));
    }

    /** 求助好友状态：指定模式今日已用次数/上限/剩余（上限读 help_max 配置） */
    @ApiName("求助好友状态接口")
    @PostMapping("/daily-help/status")
    public Result<DailyHelpResponse> dailyHelpStatus(@RequestBody ProgressRequest request) {
        Long userId = UserContext.getCurrentUserId();
        String mode = com.sniper.game.wordgame.service.UserService.normalizeHelpMode(request.getMode());
        return Result.success(new DailyHelpResponse(
                userService.getDailyHelpUsed(userId, mode), userService.getHelpMax(mode)));
    }

    /** 求助好友使用：次数+1（达上限不再自增），返回最新次数 */
    @ApiName("求助好友使用接口")
    @PostMapping("/daily-help/use")
    public Result<DailyHelpResponse> useDailyHelp(@RequestBody ProgressRequest request) {
        Long userId = UserContext.getCurrentUserId();
        String mode = com.sniper.game.wordgame.service.UserService.normalizeHelpMode(request.getMode());
        return Result.success(new DailyHelpResponse(
                userService.useDailyHelp(userId, mode), userService.getHelpMax(mode)));
    }

    @ApiName("更新资料接口")
    @PostMapping("/profile")
    public Result<Void> updateProfile(@Valid @RequestBody ProfileRequest request) {
        Long userId = UserContext.getCurrentUserId();
        userService.updateProfile(userId, request.getNickname(), request.getAvatarUrl());
        return Result.success();
    }

    @ApiName("分享次数核销接口")
    @PostMapping("/share/consume")
    public Result<ShareConsumeResponse> consumeShareCount(@RequestBody ShareConsumeRequest request) {
        Long userId = UserContext.getCurrentUserId();
        ShareConsumeResponse response = userService.consumeShareCount(userId, request.getGameType());
        return Result.success(response);
    }

    /** 地区字典列表（缓存优先）：选地区弹窗拉起时调 */
    @ApiName("地区列表接口")
    @PostMapping("/region/list")
    public Result<List<RegionService.RegionItem>> regionList() {
        return Result.success(regionService.listRegions());
    }

    /** 保存用户选的地区（存 region.id） */
    @ApiName("保存地区接口")
    @PostMapping("/region")
    public Result<Void> saveRegion(@Valid @RequestBody RegionSaveRequest request) {
        Long userId = UserContext.getCurrentUserId();
        regionService.saveUserRegion(userId, request.getRegionId());
        return Result.success();
    }

    /** 提交用户反馈：设置页"游戏反馈/意见反馈"入口，每人每天限提交 5 条 */
    @ApiName("提交反馈接口")
    @PostMapping("/feedback/submit")
    public Result<Void> submitFeedback(@Valid @RequestBody FeedbackSubmitRequest request) {
        feedbackService.submit(UserContext.getCurrentUserId(), request.getFeedbackType(), request.getContent());
        return Result.success();
    }

    /** 公共上传：图片传 OSS，只返回 CDN 地址（不落库） */
    @ApiName("资源上传接口")
    @PostMapping("/resource/upload")
    public Result<ResourceUploadResponse> uploadResource(@RequestParam("file") MultipartFile file) {
        return Result.success(resourceService.upload(file));
    }

    /** 七日签到奖励配置：7 天列表（含奖励图 URL），前端弹窗渲染用 */
    @ApiName("七日签到配置接口")
    @PostMapping("/signin/config")
    public Result<List<SignInRewardItem>> signInConfig() {
        return Result.success(resourceService.getSignInRewards());
    }

    /**
     * 签到成功上报：仅用于统计每日签到人数，前端本地判定签到成功（今天首次签到）后调用一次即可。
     * 不返回业务数据，签到状态/奖励发放仍全在前端本地处理，本接口不影响任何现有签到逻辑。
     */
    @ApiName("签到上报接口")
    @PostMapping("/signin/report")
    public Result<Void> signInReport() {
        userService.reportSignIn(UserContext.getCurrentUserId());
        return Result.success(null);
    }

    /** 资源查询：所有登记了类型编码的资源明细（前端按 resourceCode 组 Map，value=整条数据） */
    @ApiName("资源查询接口")
    @PostMapping("/resources")
    public Result<List<ResourceItem>> resources() {
        return Result.success(userService.getResourceList());
    }

    /** 每日挑战过关奖励：stage1=金币200 / stage2=道具抽1 / stage3=收集抽1，规则硬编码在 RewardService */
    @ApiName("每日挑战过关奖励接口")
    @PostMapping("/reward/daily")
    public Result<List<RewardItem>> rewardDaily(@Valid @RequestBody RewardDailyRequest request) {
        return Result.success(rewardService.dailyStageReward(
                request.getStage(), request.getOwnedCollectCodes()));
    }

    /** 无限模式过关结算：普通关=[金币]；5 的倍数关=[金币+随机道具/收集抽1] */
    @ApiName("无限模式过关结算接口")
    @PostMapping("/reward/endless")
    public Result<List<RewardItem>> rewardEndless(@Valid @RequestBody RewardEndlessRequest request) {
        return Result.success(rewardService.endlessClearReward(
                request.getLevel(), request.getOwnedCollectCodes()));
    }

    /**
     * 商城分页目录：category=1 道具/2 收集（可选按 groupCode 筛选），道具关联资源表、收集关联收集表；
     * 购买发放走前端本地账。
     */
    @ApiName("商城目录接口")
    @PostMapping("/shop/list")
    public Result<ShopPageDto> shopList(@RequestBody(required = false) ShopListRequest request) {
        return Result.success(shopService.listShop(request));
    }

    /** 收集品全量目录（只读配置，拥有/当前展示状态见 /backpack/list） 接口废弃有性能风险*/
    @ApiName("收集品目录接口")
    @PostMapping("/collect/list")
    public Result<List<CollectItemDto>> collectList() {
        return Result.success(collectService.listCatalog());
    }

    /** 随机水果目录（首页圆盘人群用，LIMIT 30 兜底） */
    @ApiName("随机水果接口")
    @PostMapping("/collect/fruits")
    public Result<List<CollectItemDto>> collectFruits() {
        return Result.success(collectService.listRandomFruits(30));
    }

    /**
     * 按 id 批量查收集品目录：本地已知目标 id（如猫咪图标查当前展示项、抽奖排除已拥有）时用这个，
     * 不必再拉 /collect/list 整表下发。
     */
    @ApiName("收集品按id查询接口")
    @PostMapping("/collect/by-ids")
    public Result<List<CollectItemDto>> collectByIds(@RequestBody(required = false) CollectByIdsRequest request) {
        return Result.success(collectService.listByIds(request == null ? null : request.getIds()));
    }

    /** 新用户默认赠送的收集品配置（未配置返回 null），补领判断用 */
    @ApiName("新用户默认收集品接口")
    @PostMapping("/collect/starter-gift")
    public Result<CollectItemDto> collectStarterGift() {
        return Result.success(collectService.getStarterGift());
    }

    /**
     * 按 collectCode 批量查收集品目录：奖励结果（RewardItem.collectCode）反查名称/id 用，
     * 不必再拉 /collect/list 整表下发。
     */
    @ApiName("收集品按code查询接口")
    @PostMapping("/collect/by-codes")
    public Result<List<CollectItemDto>> collectByCodes(@RequestBody(required = false) CollectByCodesRequest request) {
        return Result.success(collectService.listByCodes(request == null ? null : request.getCodes()));
    }

    /** 当前用户收集品背包（拥有数量/当前展示项，结合 /collect/list 目录展示） */
    @ApiName("背包列表接口")
    @PostMapping("/backpack/list")
    public Result<List<BackpackItemDto>> backpackList() {
        return Result.success(backpackService.listBackpack(UserContext.getCurrentUserId()));
    }

    /**
     * 我的仓库：目录+持有状态服务端拼好下发，按分组筛选 + 分页（默认每页 10 条）。
     * 内部两条单表 SQL 后在内存关联，不连表；仓库页专用，老的 /collect/list + /backpack/list 保留给其他页面。
     */
    @ApiName("我的仓库接口")
    @PostMapping("/backpack/my-storage")
    public Result<MyStoragePageDto> myStorage(@RequestBody(required = false) MyStorageRequest request) {
        return Result.success(backpackService.myStorage(UserContext.getCurrentUserId(), request));
    }

    /** 拥有一个收集品：累加数量（支持重复拥有），amount 未传按1处理 */
    @ApiName("背包拥有接口")
    @PostMapping("/backpack/own")
    public Result<Void> backpackOwn(@Valid @RequestBody BackpackOwnRequest request) {
        backpackService.own(UserContext.getCurrentUserId(), request.getCollectId(), request.getAmount());
        return Result.success();
    }

    /** 设置当前展示的收集品：需已拥有才生效 */
    @ApiName("背包设为当前展示接口")
    @PostMapping("/backpack/set-current")
    public Result<Void> backpackSetCurrent(@Valid @RequestBody BackpackSetCurrentRequest request) {
        backpackService.setCurrent(UserContext.getCurrentUserId(), request.getCollectId());
        return Result.success();
    }
}
