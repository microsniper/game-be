package com.sniper.game.wordgame.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sniper.game.wordgame.constant.CommonConstants;
import com.sniper.game.wordgame.constant.RedisKeyConstants;
import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import com.sniper.game.wordgame.constant.enums.SourceEnum;
import com.alibaba.fastjson.TypeReference;
import com.sniper.game.wordgame.dto.DailyClearResponse;
import com.sniper.game.wordgame.dto.DailyRankResponse;
import com.sniper.game.wordgame.dto.DailyStatusResponse;
import com.sniper.game.wordgame.dto.GameConfigResponse;
import com.sniper.game.wordgame.dto.LoginResponse;
import com.sniper.game.wordgame.dto.RankResponse;
import com.sniper.game.wordgame.dto.ResourceItem;
import com.sniper.game.wordgame.dto.ShareConsumeResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.sniper.game.wordgame.entity.GameConfig;
import com.sniper.game.wordgame.entity.GameResource;
import com.sniper.game.wordgame.entity.User;
import com.sniper.game.wordgame.entity.UserDailyChallenge;
import com.sniper.game.wordgame.entity.UserProgress;
import com.sniper.game.wordgame.exception.BusinessException;
import com.sniper.game.wordgame.mapper.GameConfigMapper;
import com.sniper.game.wordgame.mapper.GameResourceMapper;
import com.sniper.game.wordgame.mapper.UserDailyChallengeMapper;
import com.sniper.game.wordgame.mapper.UserMapper;
import com.sniper.game.wordgame.mapper.UserProgressMapper;
import com.sniper.game.wordgame.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[\\P{Cntrl}]{1,32}$");
    private static final String DEFAULT_NICKNAME = "玩家";

    private final UserMapper userMapper;
    private final UserProgressMapper userProgressMapper;
    private final UserDailyChallengeMapper userDailyChallengeMapper;
    private final GameConfigMapper gameConfigMapper;
    private final GameResourceMapper gameResourceMapper;
    private final RegionService regionService;
    private final RedisUtils redisUtils;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${wx.miniapp.appid:}")
    private String miniappAppid;

    @Value("${wx.miniapp.secret:}")
    private String miniappSecret;

    @Value("${wx.miniapp.jscode2session-url:https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code}")
    private String jscode2sessionUrl;

    @Value("${dy.miniapp.appid:}")
    private String dyMiniAppId;

    @Value("${dy.miniapp.secret:}")
    private String dyMiniAppSecret;

    @Value("${dy.miniapp.jscode2session-url:https://developer.toutiao.com/api/apps/v2/jscode2session?appid=%s&secret=%s&code=%s}")
    private String dyJscode2sessionUrl;

    public LoginResponse login(String code, GameTypeEnum gameType, SourceEnum source) {
        String openid;
        String unionid;
        if (source == SourceEnum.DOUYIN) {
            JSONObject session = getDouyinSession(code);
            openid = session.getString("openid");
            unionid = null;
        } else {
            JSONObject session = getWechatSession(code);
            openid = session.getString("openid");
            unionid = StringUtils.trimToNull(session.getString("unionid"));
        }

        if (StringUtils.isBlank(openid)) {
            throw BusinessException.badRequest("登录失败，未获取到openid");
        }

        boolean isNewUser = false;
        User user = userMapper.findByOpenidAndGameType(openid, gameType);
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setUnionid(unionid);
            user.setSource(source);
            user.setGameType(gameType);
            userMapper.insert(user);
            isNewUser = true;
        }

        UserProgress progress = userProgressMapper.findByUserIdAndGameType(user.getId(), gameType);
        if (progress == null) {
            progress = new UserProgress();
            progress.setUserId(user.getId());
            progress.setGameType(gameType);
            progress.setLevelNum(1);
            progress.setSource(source);
            userProgressMapper.insert(progress);
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        redisUtils.set(buildTokenKey(token), user.getId(), CommonConstants.TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);

        boolean hasProfile = org.apache.commons.lang3.StringUtils.isNotBlank(user.getNickname()) && org.apache.commons.lang3.StringUtils.isNotBlank(user.getAvatarUrl());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setOpenid(openid);
        response.setSource(source);
        response.setHasProfile(hasProfile);
        response.setProgress(new LoginResponse.Progress(progress.getGameType(), progress.getLevelNum()));
        response.setIsNewUser(isNewUser);
        // 已选地区ID直接回前端（没选过为 null），免得前端再单独拉一次
        response.setRegionId(user.getRegionId());

        log.info("UserID         : {}", user.getId());
        log.info("用户登录: userId={}, openid={}, isNew={}, level={}", user.getId(), openid, isNewUser, progress.getLevelNum());
        return response;
    }

    public void saveProgress(Long userId, GameTypeEnum gameType, Integer levelNum) {
        if (userId == null) {
            throw BusinessException.unauthorized("请先登录");
        }

        User user = userMapper.findById(userId);

        UserProgress progress = userProgressMapper.findByUserIdAndGameType(userId, gameType);
        if (progress == null) {
            progress = new UserProgress();
            progress.setUserId(userId);
            progress.setGameType(gameType);
            progress.setLevelNum(levelNum);
            progress.setSource(user != null ? user.getSource() : SourceEnum.WECHAT);
            userProgressMapper.insert(progress);
            return;
        }

        userProgressMapper.updateLevelNum(userId, gameType, levelNum);
    }

    public GameConfigResponse getGameConfig(GameTypeEnum gameType) {
        GameTypeEnum gt = gameType != null ? gameType : GameTypeEnum.FRUIT_PICKING;
        List<GameConfig> configs = gameConfigMapper.findByGameType(gt);

        GameConfigResponse response = new GameConfigResponse();

        for (GameConfig config : configs) {
            String key = config.getConfigKey();
            String value = config.getConfigValue();

            switch (key) {
                case "challenge_interval":
                    response.setChallengeInterval(Integer.parseInt(value));
                    break;
                case "normal_weights":
                    response.setNormalWeights(JSON.parseObject(value, GameConfigResponse.Weights.class));
                    break;
                case "challenge_weights":
                    response.setChallengeWeights(JSON.parseObject(value, GameConfigResponse.Weights.class));
                    break;
                case "box_capacity":
                    response.setBoxCapacity(JSON.parseObject(value, new TypeReference<List<GameConfigResponse.CapacityRange>>() {}));
                    break;
                case "free_coin_reward":
                    response.setFreeCoinReward(Integer.parseInt(value));
                    break;
                case "new_user_reward":
                    response.setNewUserReward(Integer.parseInt(value));
                    break;
                case "daily_challenge_wave_plan":
                    response.setDailyWavePlan(JSON.parseObject(value, new TypeReference<java.util.Map<String, GameConfigResponse.WavePlanLevel>>() {}));
                    break;
                case "daily_challenge_wave_plates":
                    response.setDailyWavePlates(JSON.parseObject(value, new TypeReference<java.util.Map<String, GameConfigResponse.WavePlatesLevel>>() {}));
                    break;
                case "daily_challenge_challenge_weights":
                    response.setDailyChallengeWeights(JSON.parseObject(value, GameConfigResponse.Weights.class));
                    break;
                case "daily_challenge_layer_rules":
                    response.setDailyLayerRules(JSON.parseObject(value, GameConfigResponse.DailyLayerRules.class));
                    break;
                case "endless_layer_rules":
                    response.setEndlessLayerRules(JSON.parseObject(value, new TypeReference<List<GameConfigResponse.EndlessLayerRuleRange>>() {}));
                    break;
                case "help_max":
                    response.setHelpMax(JSON.parseObject(value, GameConfigResponse.HelpMax.class));
                    break;
            }
        }

        return response;
    }

    public RankResponse getRankList(Long userId, GameTypeEnum gameType) {
        List<RankResponse.RankItem> topList = userProgressMapper.findTopRanks(gameType, 20);

        // 在 Java 层计算 DENSE_RANK，避免 MySQL 窗口函数全表扫描
        int rank = 0;
        int prevLevel = -1;
        for (RankResponse.RankItem item : topList) {
            if (item.getLevelNum() != prevLevel) {
                rank++;
                prevLevel = item.getLevelNum();
            }
            item.setRank(rank);
        }

        RankResponse.RankItem myRank = userProgressMapper.findUserRank(userId, gameType);
        if (myRank == null) {
            UserProgress progress = userProgressMapper.findByUserIdAndGameType(userId, gameType);
            if (progress != null) {
                User user = userMapper.findById(userId);
                int higherCount = userProgressMapper.countHigherLevels(gameType, progress.getLevelNum());
                myRank = new RankResponse.RankItem();
                myRank.setRank(higherCount + 1);
                myRank.setUserId(userId);
                myRank.setNickname(user != null ? user.getNickname() : null);
                myRank.setAvatarUrl(user != null ? user.getAvatarUrl() : null);
                myRank.setLevelNum(progress.getLevelNum());
                myRank.setIsMe(true);
            }
        }

        for (RankResponse.RankItem item : topList) {
            if (item.getUserId().equals(userId)) {
                item.setIsMe(true);
                if (myRank == null) {
                    myRank = item;
                }
            }
        }

        return new RankResponse(myRank, topList);
    }

    /**
     * 每日挑战状态：今天是否已通关（读不建行，行存在即代表当天已通关）。
     */
    public DailyStatusResponse getDailyStatus(Long userId, GameTypeEnum gameType) {
        if (userId == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        GameTypeEnum gt = gameType != null ? gameType : GameTypeEnum.FRUIT_PICKING;
        LocalDate today = LocalDate.now();
        UserDailyChallenge record = userDailyChallengeMapper.findByUserAndDate(userId, gt, today);
        Integer bestSeconds = record == null ? null : toValidSeconds(record.getStartAt(), record.getClearAt());
        return new DailyStatusResponse(record != null, today.format(DateTimeFormatter.ISO_LOCAL_DATE), bestSeconds);
    }

    /** 挑战耗时下限（秒）：低于此值判定为前端计时不可信，不参与最快成绩 */
    private static final int MIN_VALID_DURATION_SECONDS = 5;
    /** 挑战耗时上限（秒）：超过 6 小时判定为挂机或计时异常，不参与最快成绩 */
    private static final int MAX_VALID_DURATION_SECONDS = 6 * 60 * 60;

    /** 起止时间算耗时；缺失或超出合理区间返回 null */
    private Integer toValidSeconds(LocalDateTime startAt, LocalDateTime clearAt) {
        if (startAt == null || clearAt == null) {
            return null;
        }
        long seconds = java.time.Duration.between(startAt, clearAt).getSeconds();
        if (seconds < MIN_VALID_DURATION_SECONDS || seconds > MAX_VALID_DURATION_SECONDS) {
            return null;
        }
        return (int) seconds;
    }

    /**
     * 求助好友：模式归一化，仅 dailyChallenge/endlessChallenge 两值，空或非法回落 dailyChallenge
     */
    public static String normalizeHelpMode(String mode) {
        return "endlessChallenge".equals(mode) ? "endlessChallenge" : "dailyChallenge";
    }

    /**
     * 求助好友：指定模式的每日上限（help_max 配置键，缺省回落 4）
     */
    public int getHelpMax(String mode) {
        GameConfigResponse.HelpMax hm = getGameConfig(GameTypeEnum.FRUIT_PICKING).getHelpMax();
        Integer v = null;
        if (hm != null) {
            v = "endlessChallenge".equals(normalizeHelpMode(mode))
                    ? hm.getEndlessChallenge() : hm.getDailyChallenge();
        }
        return v != null && v > 0 ? v : 4;
    }

    /**
     * 求助好友：指定模式今日已用次数（Redis，当天0点自动过期，两模式分开计数）
     */
    public int getDailyHelpUsed(Long userId, String mode) {
        if (userId == null) return 0;
        String today = LocalDate.now().toString();
        String key = RedisKeyConstants.buildDailyHelpKey(userId, today, normalizeHelpMode(mode));
        Object val = redisUtils.get(key);
        if (val == null) return 0;
        try {
            return Integer.parseInt(val.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 求助好友：次数+1（Redis increment，首次自增后设TTL到当天结束）。
     * 达到上限不再自增（后端兜底限流，防绕过前端刷次数），返回当前已用次数。
     */
    public int useDailyHelp(Long userId, String mode) {
        if (userId == null) return 0;
        String m = normalizeHelpMode(mode);
        int used = getDailyHelpUsed(userId, m);
        int max = getHelpMax(m);
        if (used >= max) return used;
        String today = LocalDate.now().toString();
        String key = RedisKeyConstants.buildDailyHelpKey(userId, today, m);
        Long count = redisUtils.increment(key, 1);
        if (count != null && count == 1) {
            long secondsTillMidnight = java.time.Duration.between(java.time.LocalDateTime.now(),
                    LocalDate.now().plusDays(1).atStartOfDay()).getSeconds();
            redisUtils.expire(key, Math.max(1, secondsTillMidnight), java.util.concurrent.TimeUnit.SECONDS);
        }
        return count != null ? count.intValue() : 0;
    }

    /**
     * 资源表查询：所有登记了类型编码的资源明细列表。
     * 前端按 resourceCode 组 Map（value=整条数据），以后新增资源只插表不动代码。
     */
    public List<ResourceItem> getResourceList() {
        List<ResourceItem> result = new java.util.ArrayList<>();
        for (GameResource resource : gameResourceMapper.findAllWithCode()) {
            if (resource.getResourceCode() == null || StringUtils.isBlank(resource.getUrl())) {
                continue;
            }
            ResourceItem item = new ResourceItem();
            item.setResourceCode(resource.getResourceCode());
            item.setUrl(resource.getUrl());
            item.setName(resource.getName());
            item.setType(resource.getType());
            result.add(item);
        }
        return result;
    }

    /**
     * 每日挑战通关上报（过完第 2 关调）：写当天行，并返回本次与今日最快耗时。
     * <p>
     * region 快照取自 user 表（不接受前端传值，防通关后改省刷榜）；
     * startAt/endAt 为前端计时的挑战起止毫秒时间戳，耗时按 endAt - startAt 计（与前端「本次用时」同口径）。
     * <p>
     * uk_user_date 一人一天一行：首次通关插入；之后重复挑战若更快则刷新该行的起止时间，
     * 所以行内存的始终是当天最快那次，耗时按需相减得出，不额外存时长字段。
     * 通关人数按行统计，重复挑战不会重复计入省份榜。
     */
    public DailyClearResponse saveDailyClear(Long userId, GameTypeEnum gameType, Long startAt, Long endAt) {
        if (userId == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        GameTypeEnum gt = gameType != null ? gameType : GameTypeEnum.FRUIT_PICKING;
        User user = userMapper.findById(userId);
        if (user == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        LocalDate today = LocalDate.now();
        long nowMillis = System.currentTimeMillis();

        // 前端计时是否可信：起止齐全、顺序正确、不超前太多（容忍设备时钟偏差 2 分钟）。
        // 耗时以 endAt - startAt 计（同一部设备的钟），与前端「本次用时」口径一致，不含网络延迟
        boolean trusted = startAt != null && endAt != null && startAt > 0 && endAt >= startAt
                && startAt <= nowMillis + 120_000 && endAt <= nowMillis + 120_000;
        LocalDateTime startAtTime = trusted
                ? LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(startAt), java.time.ZoneId.systemDefault())
                : LocalDateTime.now();
        LocalDateTime clearAtTime = trusted
                ? LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(endAt), java.time.ZoneId.systemDefault())
                : LocalDateTime.now();
        Integer currentSeconds = trusted ? toValidSeconds(startAtTime, clearAtTime) : null;
        if (currentSeconds == null) {
            trusted = false;
        }

        UserDailyChallenge existing = userDailyChallengeMapper.findByUserAndDate(userId, gt, today);
        // 更新前先取出「本次挑战开始前」库里已有的最快成绩：通关页展示要用它跟本次对比，
        // 而不是更新之后再查——那样查到的会是本次自己（更新覆盖了旧记录）
        Integer previousBest = existing != null ? toValidSeconds(existing.getStartAt(), existing.getClearAt()) : null;
        boolean refreshed;
        if (existing == null) {
            UserDailyChallenge record = new UserDailyChallenge();
            record.setUserId(userId);
            record.setGameType(gt);
            record.setChallengeDate(today);
            record.setRegionId(user.getRegionId());
            record.setStartAt(startAtTime);
            record.setClearAt(clearAtTime);
            record.setSource(user.getSource() != null ? user.getSource() : SourceEnum.WECHAT);
            userDailyChallengeMapper.insert(record);
            refreshed = currentSeconds != null;
            log.info("每日挑战首次通关: userId={}, date={}, regionId={}, seconds={}",
                    userId, today, user.getRegionId(), currentSeconds);
        } else if (trusted) {
            // 更快才刷新，比较在 SQL 内完成
            refreshed = userDailyChallengeMapper.updateIfFaster(userId, gt, today, startAtTime, clearAtTime) > 0;
            log.info("每日挑战重复通关: userId={}, date={}, seconds={}, 刷新最快={}",
                    userId, today, currentSeconds, refreshed);
        } else {
            refreshed = false;
            log.info("每日挑战重复通关但计时不可信，跳过刷新: userId={}, date={}, startAt={}, endAt={}", userId, today, startAt, endAt);
        }

        // 通关页展示用的「今日最快」：有历史记录就显示历史记录（不含本次，方便对比）；
        // 今天头一次挑战没有历史记录可比，才退回显示本次成绩
        Integer bestSecondsForDisplay = previousBest != null ? previousBest : currentSeconds;
        // 「新纪录」严格定义为击败了一个已存在的历史记录：首次挑战没有可比对象，不算新纪录
        boolean newRecord = refreshed && previousBest != null && currentSeconds != null && currentSeconds < previousBest;
        return new DailyClearResponse(currentSeconds, bestSecondsForDisplay, newRecord);
    }

    /**
     * 每日挑战省份榜：当天各省通关人数排行。
     * 未选省用户不计入榜单；DENSE_RANK 并列与总榜同风格（Java 层计算，避免窗口函数）；
     * 省份名走 RegionService 字典缓存映射，不 join。
     */
    public DailyRankResponse getDailyRankList(Long userId, GameTypeEnum gameType) {
        if (userId == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        GameTypeEnum gt = gameType != null ? gameType : GameTypeEnum.FRUIT_PICKING;
        LocalDate today = LocalDate.now();

        List<DailyRankResponse.RankItem> topList = userDailyChallengeMapper.countByRegionGroup(gt, today);

        Map<Integer, String> regionNames = new HashMap<>();
        for (RegionService.RegionItem item : regionService.listRegions()) {
            regionNames.put(item.getId(), item.getName());
        }

        int rank = 0;
        int prevCount = -1;
        for (DailyRankResponse.RankItem item : topList) {
            if (item.getClearCount() != prevCount) {
                rank++;
                prevCount = item.getClearCount();
            }
            item.setRank(rank);
            item.setRegionName(regionNames.get(item.getRegionId()));
            item.setIsMe(false);
        }

        // 我的名次：按我当前选的省份找；我的省当天无人通关则排在已有名次之后、人数 0
        DailyRankResponse.RankItem myRank = null;
        User me = userMapper.findById(userId);
        if (me != null && me.getRegionId() != null) {
            for (DailyRankResponse.RankItem item : topList) {
                if (item.getRegionId().equals(me.getRegionId())) {
                    item.setIsMe(true);
                    myRank = item;
                    break;
                }
            }
            if (myRank == null) {
                myRank = new DailyRankResponse.RankItem();
                myRank.setRank(rank + 1);
                myRank.setRegionId(me.getRegionId());
                myRank.setRegionName(regionNames.get(me.getRegionId()));
                myRank.setClearCount(0);
                myRank.setIsMe(true);
            }
        }

        return new DailyRankResponse(myRank, topList);
    }

    public void updateProfile(Long userId, String nickname, String avatarUrl) {
        if (userId == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        User user = userMapper.findById(userId);
        if (user == null) {
            throw BusinessException.unauthorized("请先登录");
        }

        String normalizedNickname = StringUtils.trimToEmpty(nickname);
        String normalizedAvatarUrl = StringUtils.trimToEmpty(avatarUrl);

        if (StringUtils.isBlank(normalizedNickname)) {
            throw BusinessException.badRequest("昵称不能为空");
        }
        if (DEFAULT_NICKNAME.equals(normalizedNickname)) {
            throw BusinessException.badRequest("昵称不能使用默认名");
        }
        if (!NICKNAME_PATTERN.matcher(normalizedNickname).matches()) {
            throw BusinessException.badRequest("昵称最长32位，不能包含控制字符");
        }
        if (StringUtils.isBlank(normalizedAvatarUrl)) {
            throw BusinessException.badRequest("头像不能为空");
        }

        // 微信昵称允许重复，不再强制唯一
        userMapper.updateProfile(userId, normalizedNickname, normalizedAvatarUrl);
    }

    public ShareConsumeResponse consumeShareCount(Long userId, GameTypeEnum gameType) {
        if (userId == null) {
            throw BusinessException.unauthorized("请先登录");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        long secondsTillMidnight = java.time.Duration.between(now, midnight).getSeconds();

        String todayStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String key = String.format("game:share:count:gameType_%d:userId_%d:%s", gameType.getCode(), userId, todayStr);

        Long count = redisUtils.increment(key, 1);
        if (count != null && count == 1L) {
            redisUtils.expire(key, secondsTillMidnight, TimeUnit.SECONDS);
        }

        if (count != null && count > 5L) {
            throw new BusinessException(403, "今日求助次数已达上限");
        }

        return new ShareConsumeResponse(count != null && count >= 5L);
    }

    public Long getUserIdByToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        Object value = redisUtils.get(buildTokenKey(token));
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }

    public String getOpenidByUserId(Long userId) {
        User user = userMapper.findById(userId);
        return user != null ? user.getOpenid() : null;
    }

    private JSONObject getWechatSession(String code) {
        if (StringUtils.isAnyBlank(miniappAppid, miniappSecret)) {
            throw new BusinessException(500, "微信小程序登录配置缺失");
        }

        String requestUrl = String.format(jscode2sessionUrl, miniappAppid, miniappSecret, code);
        try {
            String response = restTemplate.getForObject(requestUrl, String.class);
            if (StringUtils.isBlank(response)) {
                throw BusinessException.badRequest("微信登录失败，请稍后重试");
            }

            JSONObject jsonObject = JSON.parseObject(response);
            Integer errcode = jsonObject.getInteger("errcode");
            if (errcode != null && errcode != 0) {
                String errmsg = jsonObject.getString("errmsg");
                log.warn("微信登录失败: errcode={}, errmsg={}", errcode, errmsg);
                throw BusinessException.badRequest("微信登录失败:" + StringUtils.defaultIfBlank(errmsg, "未知错误"));
            }
            return jsonObject;
        } catch (RestClientException e) {
            log.error("调用微信登录接口异常", e);
            throw new BusinessException(500, "微信登录失败，请稍后重试");
        }
    }

    private JSONObject getDouyinSession(String code) {
        if (StringUtils.isAnyBlank(dyMiniAppId, dyMiniAppSecret)) {
            throw new BusinessException(500, "抖音小程序登录配置缺失");
        }

        Map<String, String> body = new HashMap<>();
        body.put("appid", dyMiniAppId);
        body.put("secret", dyMiniAppSecret);
        body.put("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            String response = restTemplate.postForObject(dyJscode2sessionUrl, requestEntity, String.class);
            if (StringUtils.isBlank(response)) {
                throw BusinessException.badRequest("抖音登录失败，请稍后重试");
            }

            JSONObject jsonObject = JSON.parseObject(response);
            Integer errNo = jsonObject.getInteger("err_no");
            if (errNo != null && errNo != 0) {
                String errTips = jsonObject.getString("err_tips");
                log.warn("抖音登录失败: err_no={}, err_tips={}", errNo, errTips);
                throw BusinessException.badRequest("抖音登录失败:" + StringUtils.defaultIfBlank(errTips, "未知错误"));
            }

            JSONObject data = jsonObject.getJSONObject("data");
            if (data == null) {
                throw BusinessException.badRequest("抖音登录失败，未获取到用户数据");
            }
            return data;
        } catch (RestClientException e) {
            log.error("调用抖音登录接口异常", e);
            throw new BusinessException(500, "抖音登录失败，请稍后重试");
        }
    }

    private String buildTokenKey(String token) {
        return RedisKeyConstants.USER_LOGIN_TOKEN + token;
    }
}
