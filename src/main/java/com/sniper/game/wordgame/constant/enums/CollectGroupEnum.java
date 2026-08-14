package com.sniper.game.wordgame.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 收集品分组（game_collect.group_code 存 code 字符串）。
 * 水果=游戏区陪玩角色（原「伙伴」分组改名，猫/玩偶等拟人小伙伴）；后续豪车/房子等分组按需追加。
 */
@Getter
@AllArgsConstructor
public enum CollectGroupEnum {

    /**
     * 水果：游戏区陪玩角色（拟人猫/兜帽玩偶等）
     */
    FRUIT("fruit", "水果"),
    ;

    private final String code;

    private final String name;

    @Override
    @JsonValue
    public String toString() {
        return code;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CollectGroupEnum fromCode(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        if (StringUtils.isBlank(text)) {
            return null;
        }
        for (CollectGroupEnum group : values()) {
            if (group.code.equals(text) || group.name().equalsIgnoreCase(text)) {
                return group;
            }
        }
        throw new IllegalArgumentException("不支持的收集品分组: " + value);
    }
}
