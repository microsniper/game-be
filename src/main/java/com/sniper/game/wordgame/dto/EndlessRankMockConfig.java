package com.sniper.game.wordgame.dto;

import lombok.Data;

import java.util.List;

/**
 * 无限榜虚拟玩家配置（game_config.config_key = endless_rank_mock）。
 * 当前关数 = initialLevel + Σ(startDate 次日起的逐日确定性增长) + 今日增长按 hourlyWeights 曲线进度取整。
 * 每天仅 dailyActiveCount 人被种子选中「上线打关」（模拟不是人人天天玩），其余当天不涨。
 * 仅用于 /api/game/rank/config 接口，不影响真实数据接口 /api/game/rank。
 */
@Data
public class EndlessRankMockConfig {

    /** 增长起算日（yyyy-MM-dd）：当天展示值即 initialLevel，次日起开始累计增长 */
    private String startDate;

    /** 0-23 点每小时活跃度权重（长度 24）：决定当天增长分摊到哪些时段涨，全部虚拟玩家共用同一条曲线 */
    private List<Integer> hourlyWeights;

    /** 活跃玩家单日增长关数下限 */
    private Integer dailyGrowMin;

    /** 活跃玩家单日增长关数上限 */
    private Integer dailyGrowMax;

    /** 每天「上线打关」的虚拟玩家人数（按日期+昵称种子确定性选出，其余当天不涨） */
    private Integer dailyActiveCount;

    private List<MockPlayer> players;

    @Data
    public static class MockPlayer {
        private String nickname;
        private String avatarUrl;
        /** startDate 当天展示的初始关数 */
        private Integer initialLevel;
    }
}
