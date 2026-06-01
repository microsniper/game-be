package com.sniper.game.wordgame.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankResponse {

    private RankItem myRank;

    private List<RankItem> list;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RankItem {

        private Integer rank;

        private Long userId;

        private String nickname;

        private String avatarUrl;

        private Integer levelNum;

        private Boolean isMe;
    }
}
