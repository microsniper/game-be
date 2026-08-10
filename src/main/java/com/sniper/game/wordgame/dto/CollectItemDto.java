package com.sniper.game.wordgame.dto;

import lombok.Data;

/**
 * 收集品目录条目（game_collect 全量下发，只读配置）。
 * 拥有/当前展示状态不在此下发，由前端本地 CollectStore 维护（与道具/金币存储边界一致）。
 */
@Data
public class CollectItemDto {

    private Long id;

    /** 收集品编码（cat/dog/car/doll...） */
    private String collectCode;

    /** 分组编码（CollectGroupEnum.code） */
    private String groupCode;

    /** 分组中文名（CollectGroupEnum.name，未命中枚举时回落 groupCode 原文） */
    private String groupName;

    private String name;

    /** 灰色态图 OSS 地址 */
    private String grayUrl;

    /** 彩色态图 OSS 地址 */
    private String colorUrl;

    /** 新用户默认赠送 */
    private Boolean isStarterGift;
}
