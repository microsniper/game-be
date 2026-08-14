package com.sniper.game.wordgame.dto;

import lombok.Data;

/**
 * 用户背包条目（user_backpack 下发，前端 CollectStore 内存缓存用）。
 */
@Data
public class BackpackItemDto {

    /** 关联 game_collect.id */
    private Long collectId;

    /** 拥有数量 */
    private Integer count;

    /** 是否为当前展示项 */
    private Boolean isCurrent;
}
