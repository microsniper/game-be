package com.sniper.game.wordgame.dto;

import com.sniper.game.wordgame.constant.enums.GameRewardModeEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 过关奖励配置查询请求：查阶段 1（金币）固定奖励
 */
@Data
public class RewardConfigRequest {

    @NotNull(message = "mode 不能为空")
    private GameRewardModeEnum mode;
}
