package com.sniper.game.wordgame.constant.enums;

/**
 * 来源渠道枚举
 */
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.sniper.game.wordgame.constant.base.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@AllArgsConstructor
public enum SourceEnum implements CodeEnum {

    /**
     * 1-微信小程序
     */
    WECHAT(1, "微信小程序"),

    /**
     * 2-抖音小程序
     */
    DOUYIN(2, "抖音小程序"),
    ;

    private final Integer code;

    private final String name;

    @Override
    @JsonValue
    public Integer getCode() {
        return code;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static SourceEnum fromCode(Object value) {
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
        for (SourceEnum sourceEnum : values()) {
            if (sourceEnum.name().equalsIgnoreCase(text)) {
                return sourceEnum;
            }
        }
        throw new IllegalArgumentException("不支持的来源渠道: " + value);
    }

    private static SourceEnum fromNumber(Integer code) {
        if (code == null) {
            return null;
        }
        for (SourceEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("不支持的来源渠道: " + code);
    }
}
