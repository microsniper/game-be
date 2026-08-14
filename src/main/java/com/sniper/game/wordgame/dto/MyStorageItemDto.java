package com.sniper.game.wordgame.dto;

import lombok.Data;

/**
 * 我的仓库单条：目录配置（game_collect）+ 用户持有状态（user_backpack）在服务端内存拼装后的结果，
 * 前端拿到即可直接渲染，不需要再自己按 id 关联两份数据。
 */
@Data
public class MyStorageItemDto {

    /** game_collect.id */
    private Long collectId;

    private String collectCode;

    /** 分组编码（CollectGroupEnum.code） */
    private String groupCode;

    /** 分组中文名（未命中枚举时回落 groupCode 原文） */
    private String groupName;

    private String name;

    /** 灰色态图 OSS 地址 */
    private String grayUrl;

    /** 彩色态图 OSS 地址 */
    private String colorUrl;

    /** 拥有数量 */
    private Integer count;

    /** 是否当前展示项（服务端已算好兜底：整个仓库都没标记时，取排序后第一件） */
    private Boolean isCurrent;
}
