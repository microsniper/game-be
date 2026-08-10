package com.sniper.game.wordgame.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.sniper.game.wordgame.constant.base.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 过关奖励适用模式（game_reward_config.mode 存 code）
 */
@Getter
@AllArgsConstructor
public enum GameRewardModeEnum implements CodeEnum {

    /**
     * 1-每日挑战
     */
    DAILY_CHALLENGE(1, "每日挑战"),

    /**
     * 2-无限模式
     */
    ENDLESS_CHALLENGE(2, "无限模式"),
    ;

    private final Integer code;

    private final String name;

    @Override
    @JsonValue
    public Integer getCode() {
        return code;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static GameRewardModeEnum fromCode(Object value) {
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
        for (GameRewardModeEnum modeEnum : values()) {
            if (modeEnum.name().equalsIgnoreCase(text)) {
                return modeEnum;
            }
        }
        throw new IllegalArgumentException("不支持的奖励模式: " + value);
    }

    private static GameRewardModeEnum fromNumber(Integer code) {
        if (code == null) {
            return null;
        }
        for (GameRewardModeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("不支持的奖励模式: " + code);
    }
}
