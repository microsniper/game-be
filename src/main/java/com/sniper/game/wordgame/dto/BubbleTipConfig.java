package com.sniper.game.wordgame.dto;

import lombok.Data;

import java.util.List;

/**
 * 游戏区猫咪气泡提示配置（game_config.config_key = bubble_tips）。
 * 文案全部后台写死（含「过关率不到1%」这类数字），后端不做任何统计计算与占位符替换。
 * 节奏参数一并下发，调气泡快慢只改配置、不必发版。
 */
@Data
public class BubbleTipConfig {

    /** 文案池；下发前已滤掉 enabled=false 的项 */
    private List<Tip> tips;

    /** 进关后多久冒第一个气泡（秒） */
    private Integer firstDelaySeconds;

    /** 两个气泡之间的随机间隔下限（秒） */
    private Integer minIntervalSeconds;

    /** 两个气泡之间的随机间隔上限（秒） */
    private Integer maxIntervalSeconds;

    /** 单个气泡停留时长（秒） */
    private Integer displaySeconds;

    @Data
    public static class Tip {

        /** 气泡文案，直接展示，不做任何变量替换 */
        private String content;

        /** 权重，越大越容易被抽到；缺省或非正数按 1 算 */
        private Integer weight;

        /** 开关；null 视为开启，false 的项后端下发前就滤掉 */
        private Boolean enabled;

        /**
         * 适用模式：endless=仅无限模式，daily=仅每日挑战，all/缺省=两个模式都出。
         * 前端按当前模式自行过滤，后端原样下发。
         */
        private String mode;

        /**
         * 情景码：填了就只在对应局面触发（不参与随机轮播），缺省则进随机池。
         * basket_locked = 暂存区快满且场上还有锁定果篮 → 引导解锁果篮
         * add_tray      = 暂存区快满、果篮已全解锁、果盘还能加 → 引导加果盘
         * clear_tray    = 暂存区快满、果篮和果盘都满配 → 引导清空果盘
         * 三种情景每关合计只触发一次（取当时局面命中的那一条），同时会有引导小手指向对应按钮。
         */
        private String scene;
    }
}
