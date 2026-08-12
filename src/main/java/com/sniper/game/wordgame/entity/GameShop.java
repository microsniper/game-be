package com.sniper.game.wordgame.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商城配置（game_shop）：category=1 道具关联 game_resource.id；category=2 收集关联 game_collect.id。
 * 价格表内配置；后端只下发目录，购买发放走前端本地账。
 */
@Data
public class GameShop {

    private Long id;

    /** 分类：1=道具 2=收集 */
    private Integer category;

    /** category=1 关联 game_resource.id；category=2 关联 game_collect.id */
    private Long itemId;

    /** 价格（金币） */
    private Integer price;

    /** 同分类内排序，升序 */
    private Integer sortOrder;

    /** 1=上架 0=下架 */
    private Integer enabled;

    /** 商品说明（商城卡片小字，可选，如「可任意匹配果篮」） */
    private String itemDesc;

    private LocalDateTime createTime;
}
