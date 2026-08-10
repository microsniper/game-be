package com.sniper.game.wordgame.constant.enums;

/**
 * 反馈类型枚举
 */
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.sniper.game.wordgame.constant.base.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@AllArgsConstructor
public enum FeedbackTypeEnum implements CodeEnum {

    /**
     * 1-游戏反馈（bug/卡顿等）
     */
    GAME(1, "游戏反馈"),

    /**
     * 2-意见反馈（建议）
     */
    SUGGESTION(2, "意见反馈"),
    ;

    private final Integer code;

    private final String name;

    @Override
    @JsonValue
    public Integer getCode() {
        return code;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static FeedbackTypeEnum fromCode(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return fromNumber(((Number) value).intValue());
        }

        String text = String.valueOf(value).trim();
        if (StringUtils.isBlank(text)) {
            return null;
        }
        if (StringUtils.isNumeric(text)) {
            return fromNumber(Integer.parseInt(text));
        }
        for (FeedbackTypeEnum feedbackTypeEnum : values()) {
            if (feedbackTypeEnum.name().equalsIgnoreCase(text)) {
                return feedbackTypeEnum;
            }
        }
        throw new IllegalArgumentException("不支持的反馈类型: " + value);
    }

    private static FeedbackTypeEnum fromNumber(Integer code) {
        if (code == null) {
            return null;
        }
        for (FeedbackTypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("不支持的反馈类型: " + code);
    }
}
