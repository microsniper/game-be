package com.sniper.game.wordgame.entity;

import com.sniper.game.wordgame.constant.enums.GameRewardModeEnum;
import com.sniper.game.wordgame.constant.enums.ItemTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 过关奖励配置：按 mode + stage 区分，item_type + item_id 多态关联（不加外键）。
 * stage=1 通常只有一条（金币）；stage=2 多条候选，按 weight 加权随机抽一条。
 */
@Data
public class GameRewardConfig {

    private Long id;

    private GameRewardModeEnum mode;

    /** 领取阶段：1=金币，2=道具/皮肤按权重随机 */
    private Integer stage;

    private ItemTypeEnum itemType;

    /** itemType=PROP 时关联 game_resource.id；itemType=COLLECT 时关联 game_collect.id */
    private Long itemId;

    private Integer amount;

    /** 同一 mode+stage 内多条候选的随机权重 */
    private Integer weight;

    private LocalDateTime createTime;
}
