package com.sniper.game.wordgame.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class ProfileRequest {

    @NotBlank(message = "昵称不能为空")
    @Size(min = 1, max = 6, message = "昵称限6位")
    @Pattern(regexp = "^[A-Za-z0-9\\u4e00-\\u9fa5]{1,6}$", message = "昵称只能使用汉字、字母、数字")
    private String nickname;

    @NotBlank(message = "头像不能为空")
    private String avatarUrl;
}
