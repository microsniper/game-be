package com.sniper.game.wordgame.dto;

import com.sniper.game.wordgame.constant.enums.ItemTypeEnum;
import com.sniper.game.wordgame.constant.enums.ResourceCodeTypeEnum;
import lombok.Data;

/**
 * 阶段 2 候选池的一条记录（内部使用，含 weight 供加权随机；不直接作为接口响应）
 */
@Data
public class RewardCandidate {

    private ItemTypeEnum itemType;

    private ResourceCodeTypeEnum resourceCode;

    private String collectCode;

    private Integer amount;

    private String imageUrl;

    /** 随机权重 */
    private Integer weight;

    public RewardItem toRewardItem() {
        RewardItem item = new RewardItem();
        item.setItemType(itemType);
        item.setResourceCode(resourceCode);
        item.setCollectCode(collectCode);
        item.setAmount(amount);
        item.setImageUrl(imageUrl);
        return item;
    }
}
