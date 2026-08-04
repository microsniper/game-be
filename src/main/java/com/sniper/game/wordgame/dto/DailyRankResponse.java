package com.sniper.game.wordgame.dto;

import lombok.Data;

import java.util.List;

/**
 * 每日挑战省份榜：当天各省通关人数排行（DENSE_RANK 并列，与总榜同风格）。
 */
@Data
public class DailyRankResponse {

    private RankItem myRank;

    private List<RankItem> list;

    public DailyRankResponse() {
    }

    public DailyRankResponse(RankItem myRank, List<RankItem> list) {
        this.myRank = myRank;
        this.list = list;
    }

    @Data
    public static class RankItem {
        private Integer rank;
        private Integer regionId;
        private String regionName;
        private Integer clearCount;
        private Boolean isMe;
    }
}
