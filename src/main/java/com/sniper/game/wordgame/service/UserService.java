package com.sniper.game.wordgame.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sniper.game.wordgame.constant.CommonConstants;
import com.sniper.game.wordgame.constant.RedisKeyConstants;
import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import com.sniper.game.wordgame.constant.enums.SourceEnum;
import com.alibaba.fastjson.TypeReference;
import com.sniper.game.wordgame.dto.DailyRankResponse;
import com.sniper.game.wordgame.dto.DailyStatusResponse;
import com.sniper.game.wordgame.dto.GameConfigResponse;
import com.sniper.game.wordgame.dto.LoginResponse;
import com.sniper.game.wordgame.dto.RankResponse;
import com.sniper.game.wordgame.dto.ShareConsumeResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.sniper.game.wordgame.entity.GameConfig;
import com.sniper.game.wordgame.entity.User;
import com.sniper.game.wordgame.entity.UserDailyChallenge;
import com.sniper.game.wordgame.entity.UserProgress;
import com.sniper.game.wordgame.exception.BusinessException;
import com.sniper.game.wordgame.mapper.GameConfigMapper;
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

        // 每日登录奖励：Redis 标记不存在 = 今日未领取，可弹窗（key 格式与 claim 接口保持一致）
        String today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
        String rewardKey = RedisKeyConstants.buildDailyRewardKey(user, today);
        if (isNewUser) {
            // 新用户首日发新人见面礼，不参与每日登录奖励：直接标记今日已领（第二天起正常）
            redisUtils.setIfAbsent(rewardKey, "1", 36, java.util.concurrent.TimeUnit.HOURS);
        }
        response.setDailyRewardClaimable(!redisUtils.hasKey(rewardKey));

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
                case "tool_costs":
                    response.setToolCosts(JSON.parseObject(value, GameConfigResponse.ToolCosts.class));
                    break;
                case "daily_login_reward":
                    response.setDailyLoginReward(Integer.parseInt(value));
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
                case "daily_challenge_box_capacity":
                    response.setDailyBoxCapacity(JSON.parseObject(value, GameConfigResponse.DailyBoxCapacity.class));
                    break;
                case "daily_challenge_challenge_weights":
                    response.setDailyChallengeWeights(JSON.parseObject(value, GameConfigResponse.Weights.class));
                    break;
                case "daily_challenge_layer_rules":
                    response.setDailyLayerRules(JSON.parseObject(value, GameConfigResponse.DailyLayerRules.class));
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
        return new DailyStatusResponse(record != null, today.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    /**
     * 每日求助好友：今日已用次数（Redis，当天0点自动过期）
     */
    public int getDailyHelpUsed(Long userId) {
        if (userId == null) return 0;
        String today = LocalDate.now().toString();
        String key = RedisKeyConstants.buildDailyHelpKey(userId, today);
        Object val = redisUtils.get(key);
        if (val == null) return 0;
        try {
            return Integer.parseInt(val.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 每日求助好友：次数+1（Redis increment，首次自增后设TTL到当天结束）
     */
    public int useDailyHelp(Long userId) {
        if (userId == null) return 0;
        String today = LocalDate.now().toString();
        String key = RedisKeyConstants.buildDailyHelpKey(userId, today);
        Long count = redisUtils.increment(key, 1);
        if (count != null && count == 1) {
            long secondsTillMidnight = java.time.Duration.between(java.time.LocalDateTime.now(),
                    LocalDate.now().plusDays(1).atStartOfDay()).getSeconds();
            redisUtils.expire(key, Math.max(1, secondsTillMidnight), java.util.concurrent.TimeUnit.SECONDS);
        }
        return count != null ? count.intValue() : 0;
    }

    /**
     * 每日挑战通关上报（过完第 2 关调）：写当天行。
     * region 快照取自 user 表（不接受前端传值，防通关后改省刷榜）；
     * startAt 为前端计时的挑战开始毫秒时间戳（为空/未来/非法时兜底 now()），clear_at 取服务器时刻；
     * uk_user_date 幂等：今天已记过直接返回，重复通关不重复计。
     */
    public void saveDailyClear(Long userId, GameTypeEnum gameType, Long startAt) {
        if (userId == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        GameTypeEnum gt = gameType != null ? gameType : GameTypeEnum.FRUIT_PICKING;
        User user = userMapper.findById(userId);
        if (user == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        LocalDate today = LocalDate.now();
        if (userDailyChallengeMapper.findByUserAndDate(userId, gt, today) != null) {
            return;
        }
        long nowMillis = System.currentTimeMillis();
        LocalDateTime startAtTime = (startAt == null || startAt <= 0 || startAt > nowMillis)
                ? LocalDateTime.now()
                : LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(startAt), java.time.ZoneId.systemDefault());
        UserDailyChallenge record = new UserDailyChallenge();
        record.setUserId(userId);
        record.setGameType(gt);
        record.setChallengeDate(today);
        record.setRegionId(user.getRegionId());
        record.setStartAt(startAtTime);
        record.setSource(user.getSource() != null ? user.getSource() : SourceEnum.WECHAT);
        userDailyChallengeMapper.insert(record);
        log.info("每日挑战通关: userId={}, date={}, regionId={}, startAt={}", userId, today, user.getRegionId(), startAtTime);
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
