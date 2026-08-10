package com.sniper.game.wordgame.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收集品配置（game_collect，原 game_skin 改造）：小猫/小狗/汽车/公仔等收集品。
 * 拥有是全局的（不按模式分）；掉落归属由 game_reward_config 的 mode 池子表达，商城售卖由 game_shop category=2 表达。
 */
@Data
public class GameCollect {

    private Long id;

    /** 收集品名称 */
    private String name;

    /** OSS CDN 地址（预览图/贴图） */
    private String url;

    /** 收集品编码，唯一（cat/dog/car/doll...） */
    private String collectCode;

    /** 分组：animal=动物 car=车辆 house=建筑 doll=公仔 */
    private String groupCode;

    private LocalDateTime createTime;
}
