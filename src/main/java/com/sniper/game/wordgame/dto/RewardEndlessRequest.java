package com.sniper.game.wordgame.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 无限模式过关结算请求：level 为刚通过的关卡号（5 的倍数关叠加抽奖）
 */
@Data
public class RewardEndlessRequest {

    @NotNull(message = "level 不能为空")
    private Integer level;

    /** 玩家当前已拥有的收集品编码（collectCode），抽奖时排除；为空不排除 */
    private List<String> ownedCollectCodes;
}
