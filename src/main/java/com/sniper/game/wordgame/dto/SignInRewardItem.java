package com.sniper.game.wordgame.dto;

import com.sniper.game.wordgame.constant.enums.ItemTypeEnum;
import com.sniper.game.wordgame.constant.enums.ResourceCodeTypeEnum;
import lombok.Data;

/**
 * 签到单日奖励（配置表 JOIN 资源表/收集表的下发结构）。
 * itemType=PROP 时 rewardType 有值（取自 game_resource.resource_code），collectId/collectCode 为 null；
 * itemType=COLLECT 时反之（取自 game_collect），rewardType 为 null。
 */
@Data
public class SignInRewardItem {

    /** 签到第几天（1-7） */
    private Integer dayNum;

    /** 奖励类型：1=道具(含金币) 2=收集品，对应 ItemTypeEnum */
    private ItemTypeEnum itemType;

    /** 奖励图片 OSS CDN 地址 */
    private String imageUrl;

    /** itemType=PROP 时的奖励类型（取自 game_resource.resource_code，见 ResourceCodeTypeEnum）；JSON 序列化为小写 code，前端兼容 */
    private ResourceCodeTypeEnum rewardType;

    /** itemType=COLLECT 时的 game_collect.id（前端领取后写入本地 CollectStore 用） */
    private Long collectId;

    /** itemType=COLLECT 时的收集品编码 */
    private String collectCode;

    /** itemType=COLLECT 时的展示名称 */
    private String name;

    /** 数量 */
    private Integer amount;
}
