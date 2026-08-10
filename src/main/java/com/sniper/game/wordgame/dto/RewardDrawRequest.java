package com.sniper.game.wordgame.dto;

import com.sniper.game.wordgame.constant.enums.GameRewardModeEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 过关奖励抽取请求：按 mode+stage 从候选池按权重无放回随机抽 count 条（无副作用，不写任何用户状态）
 */
@Data
public class RewardDrawRequest {

    @NotNull(message = "mode 不能为空")
    private GameRewardModeEnum mode;

    @NotNull(message = "stage 不能为空")
    private Integer stage;

    /** 抽取条数，缺省 1；候选池不足时按实际条数返回 */
    private Integer count;
}
