package com.sniper.game.wordgame.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import com.sniper.game.wordgame.constant.enums.SourceEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {

    @NotBlank(message = "code不能为空")
    private String code;

    @JsonSetter(nulls = Nulls.SKIP)
    private GameTypeEnum gameType = GameTypeEnum.SCREW;

    @JsonSetter(nulls = Nulls.SKIP)
    private SourceEnum source = SourceEnum.WECHAT;
}
