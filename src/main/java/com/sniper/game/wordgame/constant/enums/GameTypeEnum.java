package com.sniper.game.wordgame.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.sniper.game.wordgame.constant.base.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 游戏类型枚举
 */
@Getter
@AllArgsConstructor
public enum GameTypeEnum implements CodeEnum {

    /**
     * 1-螺丝游戏
     */
    SCREW(1, "螺丝游戏"),
    ;

    private final Integer code;

    private final String name;

    @Override
    @JsonValue
    public Integer getCode() {
        return code;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static GameTypeEnum fromCode(Object value) {
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
        for (GameTypeEnum gameTypeEnum : values()) {
            if (gameTypeEnum.name().equalsIgnoreCase(text)) {
                return gameTypeEnum;
            }
        }
        throw new IllegalArgumentException("不支持的游戏类型: " + value);
    }

    private static GameTypeEnum fromNumber(Integer code) {
        if (code == null) {
            return null;
        }
        for (GameTypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("不支持的游戏类型: " + code);
    }
}
