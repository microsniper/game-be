package com.sniper.game.wordgame.dto;

import com.sniper.game.wordgame.constant.enums.FeedbackTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 提交用户反馈请求。
 */
@Data
public class FeedbackSubmitRequest {

    @NotNull(message = "反馈类型不能为空")
    private FeedbackTypeEnum feedbackType;

    @NotBlank(message = "反馈内容不能为空")
    @Size(max = 200, message = "反馈内容不能超过200字")
    // 只允许汉字/数字/英文字母/空白/常见中英文标点，不允许表情及其他特殊字符（与前端 FeedbackPage 输入过滤同一套白名单）
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9\\s,.!?;:'\"()\\[\\]{}\\-_+=@#$%^*~，。！？；：“”‘’（）【】《》「」～、…·]*$",
            message = "反馈内容包含不支持的字符，仅支持汉字、数字、英文字母及常见标点")
    private String content;
}
