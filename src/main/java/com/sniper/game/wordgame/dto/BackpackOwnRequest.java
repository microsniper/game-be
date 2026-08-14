package com.sniper.game.wordgame.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BackpackOwnRequest {

    @NotNull(message = "收集品不能为空")
    private Long collectId;

    /** 拥有数量，默认1；未传时 Service 层按1处理 */
    private Integer amount;
}
