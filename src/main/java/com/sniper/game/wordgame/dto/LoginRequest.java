package com.sniper.game.wordgame.dto;

import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import com.sniper.game.wordgame.constant.enums.SourceEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LoginRequest {

    @NotBlank(message = "code不能为空")
    private String code;

    @NotNull(message = "gameType不能为空")
    private GameTypeEnum gameType;

    @NotNull(message = "source不能为空")
    private SourceEnum source;
}
