package com.sniper.game.wordgame.dto;

import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class ProgressRequest {

    @NotNull(message = "gameType不能为空")
    private GameTypeEnum gameType;

    @NotNull(message = "levelNum不能为空")
    @Min(value = 1, message = "levelNum最小为1")
    private Integer levelNum;
}
