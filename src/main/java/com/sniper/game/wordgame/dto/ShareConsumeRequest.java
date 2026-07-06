package com.sniper.game.wordgame.dto;

import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import lombok.Data;

@Data
public class ShareConsumeRequest {

    private GameTypeEnum gameType;

    public GameTypeEnum getGameType() {
        return gameType != null ? gameType : GameTypeEnum.FRUIT_PICKING;
    }
}
