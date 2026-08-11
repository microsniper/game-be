package com.sniper.game.wordgame.dto;

import lombok.Data;

/**
 * 每日挑战通关上报响应：本次耗时 + 今日最快耗时，供通关页直接展示，不必再多请求一次。
 * <p>
 * 表内一人一天一行，存的是当天最快那次的起止时间；重复挑战更快时刷新该行。
 * bestSeconds 是「本次挑战开始前」已有的历史最快，不含本次——通关页据此把两者并排展示做对比；
 * 数据库该刷新的仍会照常刷新为本次（如果更快），只是这里返回给前端的是刷新前的旧值。
 */
@Data
public class DailyClearResponse {

    /** 本次挑战耗时（秒）；前端计时不可信时为 null */
    private Integer currentSeconds;

    /** 今日最快耗时（秒，不含本次）；今天头一次挑战没有历史记录时退回本次成绩；无有效计时为 null */
    private Integer bestSeconds;

    /** 本次是否击败了一个已存在的历史最快记录（首次挑战没有可比对象，不算新纪录） */
    private Boolean newRecord;

    public DailyClearResponse() {
    }

    public DailyClearResponse(Integer currentSeconds, Integer bestSeconds, Boolean newRecord) {
        this.currentSeconds = currentSeconds;
        this.bestSeconds = bestSeconds;
        this.newRecord = newRecord;
    }
}
