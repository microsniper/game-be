package com.sniper.game.wordgame.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.sniper.game.wordgame.constant.base.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 物品类型（game_reward_config.item_type / user_backpack.item_type 存 code）
 */
@Getter
@AllArgsConstructor
public enum ItemTypeEnum implements CodeEnum {

    /**
     * 1-道具（含金币），关联 game_resource.id
     */
    PROP(1, "道具"),

    /**
     * 2-收集品，关联 game_collect.id
     */
    COLLECT(2, "收集"),
    ;

    private final Integer code;

    private final String name;

    @Override
    @JsonValue
    public Integer getCode() {
        return code;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ItemTypeEnum fromCode(Object value) {
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
        for (ItemTypeEnum typeEnum : values()) {
            if (typeEnum.name().equalsIgnoreCase(text)) {
                return typeEnum;
            }
        }
        throw new IllegalArgumentException("不支持的物品类型: " + value);
    }

    private static ItemTypeEnum fromNumber(Integer code) {
        if (code == null) {
            return null;
        }
        for (ItemTypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("不支持的物品类型: " + code);
    }
}
