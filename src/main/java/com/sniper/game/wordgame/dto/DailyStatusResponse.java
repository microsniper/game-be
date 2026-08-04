package com.sniper.game.wordgame.dto;

import lombok.Data;

/**
 * 每日挑战状态：今天是否已通关（cleared=true 则前端提示明日再来）。
 */
@Data
public class DailyStatusResponse {

    private Boolean cleared;

    /** 服务器当天日期（ISO，如 2026-08-02），前端展示/对时用 */
    private String challengeDate;

    /** 今日最快通关耗时（秒）；未通关或无有效计时为 null */
    private Integer bestSeconds;

    public DailyStatusResponse() {
    }

    public DailyStatusResponse(Boolean cleared, String challengeDate) {
        this.cleared = cleared;
        this.challengeDate = challengeDate;
    }

    public DailyStatusResponse(Boolean cleared, String challengeDate, Integer bestSeconds) {
        this.cleared = cleared;
        this.challengeDate = challengeDate;
        this.bestSeconds = bestSeconds;
    }
}
