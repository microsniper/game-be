package com.sniper.game.wordgame.constant.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 资源类型编码（game_resource.resource_code 存 code 小写值）。
 * 资源是什么类型是资源本身的属性：签到奖励经 resource_id 关联取得类型，
 * 其他按 code 查资源的场景（如特殊果弹窗取图）也用同一套编码。
 */
public enum ResourceCodeTypeEnum {

    /** 小太阳 */
    SUN("sun", "小太阳"),
    /** 砸板子道具 */
    SMASH("smash", "砸板子道具"),
    /** 清空果盘道具 */
    CLEAR("clear", "清空果盘道具"),
    /** 加果篮道具 */
    ADD("add", "加果篮道具"),
    /** 彩虹果 */
    RAINBOW("rainbow", "彩虹果"),
    /** 炸弹果 */
    BOMB("bomb", "炸弹果"),
    /** 彩虹果+炸弹果组合（按 amount 各发一份） */
    COMBO("rainbow-bomb", "彩虹果+炸弹果");

    private final String code;
    private final String desc;

    ResourceCodeTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /** JSON 序列化输出小写 code（前端按 sun/smash/... 匹配） */
    @JsonValue
    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /** 按库里的 code 反查枚举；未知类型直接抛错，配置错误尽早暴露 */
    public static ResourceCodeTypeEnum fromCode(String code) {
        for (ResourceCodeTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的资源类型编码: " + code);
    }
}
