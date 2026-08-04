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

    public DailyStatusResponse() {
    }

    public DailyStatusResponse(Boolean cleared, String challengeDate) {
        this.cleared = cleared;
        this.challengeDate = challengeDate;
    }
}
