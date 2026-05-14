package com.sniper.game.wordgame.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.sniper.game.wordgame.constant.base.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

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

    @JsonCreator
    public static GameTypeEnum fromCode(Integer code) {
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
