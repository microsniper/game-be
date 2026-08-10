package com.sniper.game.wordgame.dto;

import com.sniper.game.wordgame.constant.enums.ItemTypeEnum;
import com.sniper.game.wordgame.constant.enums.ResourceCodeTypeEnum;
import lombok.Data;

/**
 * 一条奖励结果（阶段 1 固定奖励 / 阶段 2 抽签结果通用）。
 * itemType=PROP 时 resourceCode 有值、collectCode 为 null；itemType=COLLECT 时反之。
 */
@Data
public class RewardItem {

    private ItemTypeEnum itemType;

    /** itemType=PROP 时的资源编码（取自 game_resource.resource_code，数字 code） */
    private ResourceCodeTypeEnum resourceCode;

    /** itemType=COLLECT 时的收集品编码（取自 game_collect.collect_code） */
    private String collectCode;

    private Integer amount;

    /** 奖励图 OSS CDN 地址 */
    private String imageUrl;
}
