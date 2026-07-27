package com.sniper.game.wordgame.dto;

import lombok.Data;

/**
 * 每日登录奖励领取响应
 *
 * @author sniper
 */
@Data
public class DailyRewardResponse {

    private Boolean success;

    private Integer amount;
}
