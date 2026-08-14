package com.sniper.game.wordgame.dto;

import lombok.Data;

import java.util.List;

/**
 * 每日排行榜虚拟基数配置（game_config.config_key = daily_rank_mock）。
 * 展示值 = 按 regions 区间随机取当日目标值、结合 hourlyWeights 曲线折算出的虚拟基数 + 当天该省真实通关人数。
 * 仅用于 /api/game/daily/rank/config 接口，不影响真实数据接口 /api/game/daily/rank。
 */
@Data
public class DailyRankMockConfig {

    /** 0-23 点每小时活跃度权重（长度 24），数字越大该时段虚拟基数涨得越快，全部省份共用同一条曲线 */
    private List<Integer> hourlyWeights;

    private List<RegionMock> regions;

    @Data
    public static class RegionMock {
        /** 须与 region 表 name 字段完全一致 */
        private String regionName;
        /** 当日目标值随机区间下限 */
        private Integer baselineMin;
        /** 当日目标值随机区间上限 */
        private Integer baselineMax;
    }
}
