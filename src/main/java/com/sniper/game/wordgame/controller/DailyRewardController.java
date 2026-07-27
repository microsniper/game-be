package com.sniper.game.wordgame.controller;

import com.sniper.game.wordgame.dto.DailyRewardResponse;
import com.sniper.game.wordgame.entity.GameConfig;
import com.sniper.game.wordgame.entity.User;
import com.sniper.game.wordgame.mapper.GameConfigMapper;
import com.sniper.game.wordgame.mapper.UserMapper;
import com.sniper.game.wordgame.util.RedisUtils;
import com.sniper.game.wordgame.util.UserContext;
import com.sniper.game.wordgame.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 每日登录奖励
 *
 * @author sniper
 */
@Slf4j
@RestController
@RequestMapping("/api/game/daily-reward")
@RequiredArgsConstructor
public class DailyRewardController {

    private final RedisUtils redisUtils;
    private final GameConfigMapper gameConfigMapper;
    private final UserMapper userMapper;

    private static final String CONFIG_KEY = "daily_login_reward";
    private static final int DEFAULT_REWARD = 200;

    /**
     * 领取每日登录奖励
     */
    @PostMapping("/claim")
    public Result<DailyRewardResponse> claim() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        User user = userMapper.findById(userId);
        if (user == null) {
            return Result.error(401, "用户不存在");
        }

        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String rewardKey = "game:daily_reward:source_" + user.getSource().name()
                + ":game_type_" + user.getGameType().name()
                + ":" + today
                + ":user_id_" + userId;

        // 原子操作：防止并发重复领取
        boolean claimed = redisUtils.setIfAbsent(rewardKey, "1", 36, TimeUnit.HOURS);
        if (!claimed) {
            log.info("每日奖励已领取过: userId={}", userId);
            DailyRewardResponse response = new DailyRewardResponse();
            response.setSuccess(false);
            response.setAmount(0);
            return Result.success(response);
        }

        // 从 game_config 读取奖励金额
        int amount = DEFAULT_REWARD;
        try {
            GameConfig config = gameConfigMapper.findByConfigKey(CONFIG_KEY);
            if (config != null && config.getConfigValue() != null) {
                amount = Integer.parseInt(config.getConfigValue());
            }
        } catch (Exception e) {
            log.warn("读取每日奖励配置失败，使用默认值: {}", e.getMessage());
        }

        log.info("每日登录奖励领取成功: userId={}, amount={}", userId, amount);

        DailyRewardResponse response = new DailyRewardResponse();
        response.setSuccess(true);
        response.setAmount(amount);
        return Result.success(response);
    }
}
