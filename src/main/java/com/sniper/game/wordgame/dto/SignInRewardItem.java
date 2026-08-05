package com.sniper.game.wordgame.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import com.sniper.game.wordgame.constant.enums.RewardTypeEnum;
import lombok.Data;

/**
 * 签到单日奖励（配置表 JOIN 资源表的下发结构）
 */
@Data
public class SignInRewardItem {

    /** 签到第几天（1-7） */
    private Integer dayNum;

    /** 奖励图片 OSS CDN 地址 */
    private String imageUrl;

    /** 奖励类型（枚举，见 RewardTypeEnum）；JSON 序列化为小写 code，前端兼容 */
    private RewardTypeEnum rewardType;

    /** 数量 */
    private Integer amount;
}
