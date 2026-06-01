package com.sniper.game.wordgame.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import lombok.Data;

@Data
public class RankRequest {

    @JsonSetter(nulls = Nulls.SKIP)
    private GameTypeEnum gameType = GameTypeEnum.SCREW;
}
