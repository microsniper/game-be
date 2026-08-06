package com.sniper.game.wordgame.dto;

import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class ProgressRequest {

    private GameTypeEnum gameType;

    /** 求助计数模式：dailyChallenge/endlessChallenge（仅 daily-help 接口使用，缺省 dailyChallenge） */
    private String mode;

    @NotNull(message = "levelNum不能为空")
    @Min(value = 1, message = "levelNum最小为1")
    private Integer levelNum;

    public GameTypeEnum getGameType() {
        return gameType != null ? gameType : GameTypeEnum.FRUIT_PICKING;
    }
}
