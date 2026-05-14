package com.sniper.game.wordgame.dto;

import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import com.sniper.game.wordgame.constant.enums.SourceEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;

    private SourceEnum source;

    private Progress progress;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Progress {

        private GameTypeEnum gameType;

        private Integer levelNum;
    }
}
