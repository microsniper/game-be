package com.sniper.game.wordgame.service;

import com.sniper.game.wordgame.constant.RedisKeyConstants;
import com.sniper.game.wordgame.entity.GameConfig;
import com.sniper.game.wordgame.mapper.GameConfigMapper;
import com.sniper.game.wordgame.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 埋点定时汇总上报（每10分钟向飞书推送一次）
 * <p>
 * 通知范围由 game_config 表 event_report_notify_scope 控制：
 * off=都不通知，prod=只生产通知，all=生产测试都通知（默认 prod）
 *
 * @author sniper
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventReportScheduler {

    private static final String CONFIG_KEY_NOTIFY_SCOPE = "event_report_notify_scope";
    private static final String NOTIFY_SCOPE_OFF = "off";
    private static final String NOTIFY_SCOPE_ALL = "all";

    private final RedisUtils redisUtils;
    private final FeishuNotifyService feishuNotifyService;
    private final GameConfigMapper gameConfigMapper;
    private final Environment environment;

    /**
     * 广告场景，需与前端 GameManager.showAdThen 传入的 scene 一一对应。
     * 前端新增场景后必须在此登记，否则该场景的数据只进 Redis、不进汇总通知。
     */
    private static final Map<String, String> SCENE_NAMES = new LinkedHashMap<>();

    static {
        SCENE_NAMES.put("revive", "复活");
        SCENE_NAMES.put("clear_tray", "清空果盘");
        SCENE_NAMES.put("unlock_basket", "解锁果篮");
        SCENE_NAMES.put("add_tray", "加果盘");
        SCENE_NAMES.put("get_special_fruit", "获取特殊果");
        SCENE_NAMES.put("free_coin", "免费金币");
    }

    /**
     * 广告中途关闭/跳过的场景 key（前端统一上报为 场景名 + "_skip"）。
     * 由 SCENE_NAMES 推导，避免新增场景时漏登记导致跳过数漏统计。
     */
    private static final Map<String, String> SKIP_SCENES = new LinkedHashMap<>();

    static {
        SCENE_NAMES.forEach((key, name) -> SKIP_SCENES.put(key + "_skip", name + "跳过"));
    }

    /**
     * 每10分钟执行一次（00、10、20、30、40、50分）
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void reportSummary() {
        // 当前环境标识
        String currentProfile = resolveCurrentProfile();
        String envLabel = "prod".equals(currentProfile) ? "生产" : "测试";

        // 读取通知范围开关（默认 prod）
        String notifyScope = resolveNotifyScope();
        log.info("定时任务执行: profile={}, notifyScope={}", currentProfile, notifyScope);

        if (NOTIFY_SCOPE_OFF.equals(notifyScope)) {
            log.info("定时任务跳过: 通知开关已关闭");
            return;
        }
        if (!NOTIFY_SCOPE_ALL.equals(notifyScope) && !"prod".equals(currentProfile)) {
            log.info("定时任务跳过: 当前为测试环境且只生产通知");
            return;
        }

        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String dailyKey = "game:event:daily:" + today;
        // 游标按环境隔离，避免生产/测试双实例共用 Redis 互相干扰 delta
        String lastReportKey = "game:event:last_reported:" + currentProfile + ":" + today;

        // 当前累计总数
        Object currentObj = redisUtils.get(dailyKey);
        long currentTotal = currentObj == null ? 0 : Long.parseLong(currentObj.toString());

        // 上次上报时的累计数
        Object lastObj = redisUtils.get(lastReportKey);
        long lastReported = lastObj == null ? 0 : Long.parseLong(lastObj.toString());

        log.info("定时任务执行: currentTotal={}, lastReported={}", currentTotal, lastReported);

        long delta = currentTotal - lastReported;
        if (currentTotal <= 0) {
            log.info("定时任务跳过: 今日无埋点数据");
            return;
        }

        // 统计各场景本周期累计
        StringBuilder sceneDetail = new StringBuilder();
        for (Map.Entry<String, String> entry : SCENE_NAMES.entrySet()) {
            String sceneKey = "game:event:daily:" + today + ":" + entry.getKey();
            Object sceneObj = redisUtils.get(sceneKey);
            long sceneCount = sceneObj == null ? 0 : Long.parseLong(sceneObj.toString());
            sceneDetail.append(String.format("        %s：%d 次\n", entry.getValue(), sceneCount));
        }

        // 统计广告中途关闭/跳过总次数
        long skipTotal = 0;
        for (String skipScene : SKIP_SCENES.keySet()) {
            String skipKey = "game:event:daily:" + today + ":" + skipScene;
            Object skipObj = redisUtils.get(skipKey);
            skipTotal += skipObj == null ? 0 : Long.parseLong(skipObj.toString());
        }

        // 今日日活（独立登录用户数，Redis Set 去重，SCARD 直接取当前个数）
        long dau = redisUtils.sCard(RedisKeyConstants.buildDauKey(today));
        // 今日签到人数（独立签到用户数，同样是 Redis Set 去重）
        long signInCount = redisUtils.sCard(RedisKeyConstants.buildSignInKey(today));
        // 每日挑战入口人数（独立挑战用户数，daily-help/status 接口 mode=dailyChallenge 时 SADD 去重）
        long dailyChallengeCount = redisUtils.sCard(RedisKeyConstants.buildDailyChallengeKey(today));
        // 无限模式入口人数（独立进入用户数，daily-help/status 接口 mode=endlessChallenge 时 SADD 去重）
        long endlessChallengeCount = redisUtils.sCard(RedisKeyConstants.buildEndlessChallengeKey(today));

        // 当前时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年M月d日 HH:mm:ss"));

        // 发送汇总通知
        feishuNotifyService.sendSummaryNotify(envLabel, now, delta, currentTotal, sceneDetail.toString().trim(), skipTotal, dau, signInCount, dailyChallengeCount, endlessChallengeCount);

        // 更新上次上报计数
        redisUtils.set(lastReportKey, currentTotal, 36, TimeUnit.HOURS);

        log.info("定时汇总上报完成: env={}, delta={}, dailyTotal={}, skip={}, dailyChallenge={}", envLabel, delta, currentTotal, skipTotal, dailyChallengeCount);
    }

    /**
     * 解析当前环境标识，默认生产环境
     */
    private String resolveCurrentProfile() {
        if (environment != null && environment.getActiveProfiles().length > 0) {
            return environment.getActiveProfiles()[0];
        }
        return "prod";
    }

    /**
     * 从 game_config 读取通知范围开关，默认只生产通知
     */
    private String resolveNotifyScope() {
        try {
            GameConfig config = gameConfigMapper.findByConfigKey(CONFIG_KEY_NOTIFY_SCOPE);
            if (config != null && config.getConfigValue() != null) {
                String value = config.getConfigValue().trim().toLowerCase();
                if (NOTIFY_SCOPE_OFF.equals(value) || NOTIFY_SCOPE_ALL.equals(value)) {
                    return value;
                }
            }
        } catch (Exception e) {
            log.warn("读取通知范围开关失败，使用默认值 prod", e);
        }
        return "prod";
    }
}
