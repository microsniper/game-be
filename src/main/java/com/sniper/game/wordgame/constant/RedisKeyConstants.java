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
}
