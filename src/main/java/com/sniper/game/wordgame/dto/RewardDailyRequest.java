package com.sniper.game.wordgame.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 每日挑战过关奖励请求：stage 1=金币 2=道具抽 3=收集抽
 */
@Data
public class RewardDailyRequest {

    @NotNull(message = "stage 不能为空")
    private Integer stage;

    /** 玩家当前已拥有的收集品编码（collectCode），抽奖时排除；为空不排除 */
    private List<String> ownedCollectCodes;
}
