package com.sniper.game.wordgame.dto;

import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import com.sniper.game.wordgame.constant.enums.SourceEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponse {

    private String token;

    private String openid;

    private SourceEnum source;

    private Boolean hasProfile;

    private Progress progress;

    private Boolean isNewUser;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Progress {

        private GameTypeEnum gameType;

        private Integer levelNum;
    }
}
