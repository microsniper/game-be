package com.sniper.game.wordgame.constant;

/**
 * Redis Key 常量
 */
public class RedisKeyConstants {

    private RedisKeyConstants() {}

    /**
     * 用户登录 Token 前缀
     */
    public static final String USER_LOGIN_TOKEN = "game:user:login:";

    /**
     * 求助好友次数 key（每天每用户每模式独立计数，TTL 到当天结束）。
     * mode 区分 dailyChallenge/endlessChallenge，两模式额度互不影响。
     */
    public static String buildDailyHelpKey(Long userId, String today, String mode) {
        return "game:daily_help:" + today + ":user_id_" + userId + ":" + mode;
    }

    /**
     * 首页地区排行榜人物圆盘随机头像池 key（全局共享一份，固定 3 小时，避免每次请求都 order by rand()）。
     * limit 拼进 key：调用方传参变化时不会读到条数不匹配的旧缓存。
     */
    public static String buildRegionRankPersonKey(int limit) {
        return "game:region:rank:person:random:" + limit;
    }

    /**
     * 日活统计 key（Redis Set，登录时 SADD userId 去重，SCARD 取当天独立用户数）。
     * date 传 yyyy-MM-dd，次日自动过期，不用额外清理任务。
     */
    public static String buildDauKey(String date) {
        return "game:dau:" + date;
    }

    /**
     * 每日签到人数统计 key（Redis Set，签到成功上报时 SADD userId 去重，SCARD 取当天独立签到人数）。
     * date 传 yyyy-MM-dd，次日自动过期，不用额外清理任务。纯统计用途，不做防重复签到的业务校验。
     */
    public static String buildSignInKey(String date) {
        return "game:sign_in:" + date;
    }

    /**
     * 每日挑战入口人数统计 key（Redis Set，进入每日挑战对局时 SADD userId 去重，SCARD 取当天独立挑战人数）。
     * date 传 yyyy-MM-dd，次日自动过期，不用额外清理任务。
     * 埋点挂在求助状态接口上：前端 LoadingPage 进每日挑战必调 daily-help/status(mode=dailyChallenge)，
     * 复用这个请求顺带 SADD，避免前端单独发一次埋点请求、也避免新增发版。
     */
    public static String buildDailyChallengeKey(String date) {
        return "game:daily_challenge:" + date;
    }

    /**
     * 无限模式入口人数统计 key（Redis Set，进入无限模式对局时 SADD userId 去重，SCARD 取当天独立进入人数）。
     * date 传 yyyy-MM-dd，次日自动过期，不用额外清理任务。
     * 埋点挂在求助状态接口上：前端 LoadingPage 进无限模式必调 daily-help/status(mode=endlessChallenge)，
     * 复用这个请求顺带 SADD，与每日挑战入口统计口径一致。
     */
    public static String buildEndlessChallengeKey(String date) {
        return "game:endless_challenge:" + date;
    }
}
