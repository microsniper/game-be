package com.sniper.game.wordgame.constant.enums;

/**
 * 来源渠道枚举
 */
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.sniper.game.wordgame.constant.base.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SourceEnum implements CodeEnum {

    /**
     * 1-微信小程序
     */
    WECHAT(1, "微信小程序"),

    /**
     * 2-支付宝小程序
     */
    ALIPAY(2, "支付宝小程序"),

    /**
     * 3-H5网页
     */
    H5(3, "H5网页"),
    ;

    private final Integer code;

    private final String name;

    @Override
    @JsonValue
    public Integer getCode() {
        return code;
    }

    @JsonCreator
    public static SourceEnum fromCode(Integer code) {
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
