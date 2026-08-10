package com.sniper.game.wordgame.dto;

import com.sniper.game.wordgame.constant.enums.FeedbackTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 提交用户反馈请求。
 */
@Data
public class FeedbackSubmitRequest {

    @NotNull(message = "反馈类型不能为空")
    private FeedbackTypeEnum feedbackType;

    @NotBlank(message = "反馈内容不能为空")
    @Size(max = 500, message = "反馈内容不能超过500字")
    private String content;
}
