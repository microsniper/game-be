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
    }
}
