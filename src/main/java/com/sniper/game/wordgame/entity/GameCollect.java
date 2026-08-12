package com.sniper.game.wordgame.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收集品配置（game_collect，原 game_skin 改造）：小猫/小狗/汽车/公仔等收集品。
 * 拥有是全局的（不按模式分）；掉落归属由 RewardService 硬编码抽奖池表达，商城售卖由 game_shop category=2 表达。
 */
@Data
public class GameCollect {

    private Long id;

    /** 收集品名称 */
    private String name;

    /** 收集品编码，唯一（cat/dog/car/doll...） */
    private String collectCode;

    /** 分组：animal=动物 car=车辆 house=建筑 doll=公仔 */
    private String groupCode;

    /** 灰色态图 OSS 地址（未点亮/仓库未拥有展示） */
    private String grayUrl;

    /** 彩色态图 OSS 地址（点亮/已拥有展示） */
    private String colorUrl;

    /** 新用户默认赠送（全表应只有一行为true） */
    private Boolean isStarterGift;

    private LocalDateTime createTime;
}
