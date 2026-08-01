package com.sniper.game.wordgame.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 保存用户地区请求：regionId 是 region 表主键。
 * 合法性（是否 34 个之一）由 Service 层对照字典校验。
 */
@Data
public class RegionSaveRequest {

    @NotNull(message = "地区不能为空")
    private Integer regionId;
}
