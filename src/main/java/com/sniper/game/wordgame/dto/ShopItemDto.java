package com.sniper.game.wordgame.dto;

import com.sniper.game.wordgame.constant.enums.ResourceCodeTypeEnum;
import lombok.Data;

/**
 * 商城目录条目（game_shop JOIN game_resource/game_collect 下发）。
 * category=1 时 resourceCode 有值（前端按它入账）；category=2 时 groupCode/collect 信息有值。
 */
@Data
public class ShopItemDto {

    private Long id;

    /** 分类：1=道具 2=收集 */
    private Integer category;

    /** 价格（金币） */
    private Integer price;

    private Integer sortOrder;

    /** category=1 时的资源编码（数字 code），category=2 时为 null */
    private ResourceCodeTypeEnum resourceCode;

    /** category=2 时的收集品分组编码（CollectGroupEnum.code），category=1 时为空串 */
    private String groupCode;

    /** category=2 时的收集品分组中文名（CollectGroupEnum.name），category=1 时为空串 */
    private String groupName;

    /** 展示名称（资源表/收集表） */
    private String name;

    /** 展示图 OSS 地址 */
    private String imageUrl;

    /** category=2 时的 game_collect.id（前端购买成功后写入本地 CollectStore 用），category=1 时为 null */
    private Long collectId;

    /** 商品说明（商城卡片小字，可选） */
    private String itemDesc;
}
