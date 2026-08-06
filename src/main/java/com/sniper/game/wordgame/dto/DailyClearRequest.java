package com.sniper.game.wordgame.dto;

import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import lombok.Data;

/**
 * 每日挑战通关上报：startAt/endAt 均为前端计时的毫秒时间戳（开始/过关瞬间）。
 * 耗时以 endAt - startAt 计算（同一部设备的钟，口径与前端「本次用时」一致），
 * 避免用服务器收到请求的时刻做终点导致多出网络延迟。
 */
@Data
public class DailyClearRequest {

    private GameTypeEnum gameType;

    /** 挑战开始时间（前端毫秒时间戳；为空/非法时后端兜底 now()） */
    private Long startAt;

    /** 挑战过关时间（前端毫秒时间戳；为空/非法时后端兜底 now()） */
    private Long endAt;
}
