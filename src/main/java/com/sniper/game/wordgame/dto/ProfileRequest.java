package com.sniper.game.wordgame.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ProfileRequest {

    /** 微信昵称：最长32位，可含符号/emoji/空格，字符合法性由 Service 层兜底校验 */
    @NotBlank(message = "昵称不能为空")
    @Size(min = 1, max = 32, message = "昵称限32位")
    private String nickname;

    @NotBlank(message = "头像不能为空")
    @Size(max = 512, message = "头像地址过长")
    private String avatarUrl;
}
