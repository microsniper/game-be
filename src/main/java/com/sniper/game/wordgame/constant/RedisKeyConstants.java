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
     * 每日登录奖励领取标记 key（login 校验与 claim 领取必须使用同一格式）
     */
    public static String buildDailyRewardKey(com.sniper.game.wordgame.entity.User user, String today) {
        return "game:daily_reward:source_" + user.getSource().name()
                + ":game_type_" + user.getGameType().name()
                + ":" + today
                + ":user_id_" + user.getId();
    }
}
