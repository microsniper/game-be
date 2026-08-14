package com.sniper.game.wordgame.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BackpackSetCurrentRequest {

    @NotNull(message = "收集品不能为空")
    private Long collectId;
}
