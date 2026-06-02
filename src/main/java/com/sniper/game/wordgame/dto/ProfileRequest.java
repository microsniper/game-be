package com.sniper.game.wordgame.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ProfileRequest {

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @NotBlank(message = "头像不能为空")
    private String avatarUrl;
}
