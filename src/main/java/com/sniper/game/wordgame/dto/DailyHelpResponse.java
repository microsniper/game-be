package com.sniper.game.wordgame.dto;

import lombok.Data;

/**
 * 每日求助好友次数响应
 */
@Data
public class DailyHelpResponse {
    /** 今日已用次数 */
    private int used;
    /** 每日上限 */
    private int max;
    /** 剩余次数 */
    private int remaining;

    public DailyHelpResponse(int used, int max) {
        this.used = used;
        this.max = max;
        this.remaining = Math.max(0, max - used);
    }
}
