package com.sniper.game.wordgame.dto;

import lombok.Data;
import java.util.List;

@Data
public class GameConfigResponse {

    private Integer challengeInterval;

    private Weights normalWeights;

    private Weights challengeWeights;

    private List<CapacityRange> boxCapacity;

    private ToolCosts toolCosts;

    private Integer dailyLoginReward;

    private Integer newUserReward;

    /** 每日挑战批次计划（按关号分组）：levels.关号.batches=[{colors水果颜色数,layers层数}] */
    private java.util.Map<String, WavePlanLevel> dailyWavePlan;

    /** 每日挑战每层板子数（按关号分组再按批） */
    private java.util.Map<String, WavePlatesLevel> dailyWavePlates;

    /** 每日挑战果篮孔数分布权重 */
    private DailyBoxCapacity dailyBoxCapacity;

    /** 每日挑战果篮刷新颜色权重 */
    private Weights dailyChallengeWeights;

    /** 每日挑战层流规则：遮挡翻彩/补层阈值 */
    private DailyLayerRules dailyLayerRules;

    /** 无限模式层流规则（按关卡区间）：max=关卡上界，字段缺省回落前端默认值 */
    private List<EndlessLayerRuleRange> endlessLayerRules;

    @Data
    public static class Weights {
        private Integer temp;
        private Integer click;
        private Integer block;
    }

    @Data
    public static class CapacityRange {
        private Integer max;
        private Integer w3;
        private Integer w4;
        private Integer w5;
        private Integer w6;
    }

    @Data
    public static class ToolCosts {
        private Integer addBasket;
        private Integer clearTray;
        private Integer smashPlate;
    }

    @Data
    public static class WavePlanLevel {
        private List<WavePlanBatch> batches;
    }

    @Data
    public static class WavePlanBatch {
        private Integer colors;
        private Integer layers;
    }

    @Data
    public static class WavePlatesLevel {
        private List<WavePlatesBatch> batches;
    }

    @Data
    public static class WavePlatesBatch {
        private Integer maxPlates;
        private Integer rectFirst;
        private Integer shapeFirst;
        /** 长条形大板保底块数（plate_bar，宽扁横条横向5孔；缺省/0=不出现） */
        private Integer stripFirst;
    }

    @Data
    public static class DailyBoxCapacity {
        private Integer w3;
        private Integer w4;
        private Integer w5;
        private Integer w6;
    }

    @Data
    public static class DailyLayerRules {
        private Double unburyRatio;
        private Double refillRatio;
    }

    @Data
    public static class EndlessLayerRuleRange {
        private Integer max;
        private Integer maxPlates;
        private Integer maxLayers;
        private Integer initialLoad;
        private Double refillRatio;
        private Double unburyRatio;
    }
}
