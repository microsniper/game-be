package com.sniper.game.wordgame.dto;

import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import lombok.Data;

/**
 * 每日挑战通关上报：startAt 为前端计时的挑战开始毫秒时间戳（可空，后端校验兜底）。
 */
@Data
public class DailyClearRequest {

    private GameTypeEnum gameType;

    /** 挑战开始时间（前端毫秒时间戳；为空/非法时后端兜底 now()） */
    private Long startAt;
}
