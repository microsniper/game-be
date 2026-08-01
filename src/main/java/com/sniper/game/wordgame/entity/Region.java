package com.sniper.game.wordgame.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 地区字典（省级行政区），对应 region 表。
 * 公共字典，跟游戏无关，所以不带 game_type。
 */
@Data
public class Region {

    private Integer id;

    private String name;

    /** 行政区划代码（GB/T 2260 两位） */
    private String code;

    private Integer sortOrder;

    private Integer enabled;

    private LocalDateTime createdAt;
}
