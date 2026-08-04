package com.sniper.game.wordgame.dto;

import lombok.Data;

/**
 * 每日挑战通关上报响应：本次耗时 + 今日最快耗时，供通关页直接展示，不必再多请求一次。
 * <p>
 * 表内一人一天一行，存的是当天最快那次的起止时间；重复挑战更快时刷新该行。
 */
@Data
public class DailyClearResponse {

    /** 本次挑战耗时（秒）；前端计时不可信时为 null */
    private Integer currentSeconds;

    /** 今日最快耗时（秒）；无有效计时记录时为 null */
    private Integer bestSeconds;

    /** 本次是否刷新了今日最快 */
    private Boolean newRecord;

    public DailyClearResponse() {
    }

    public DailyClearResponse(Integer currentSeconds, Integer bestSeconds, Boolean newRecord) {
        this.currentSeconds = currentSeconds;
        this.bestSeconds = bestSeconds;
        this.newRecord = newRecord;
    }
}
