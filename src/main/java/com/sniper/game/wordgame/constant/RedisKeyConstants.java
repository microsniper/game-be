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
     * 每日求助好友次数 key（每天每用户独立计数，TTL 到当天结束）
     */
    public static String buildDailyHelpKey(Long userId, String today) {
        return "game:daily_help:" + today + ":user_id_" + userId;
    }
}
