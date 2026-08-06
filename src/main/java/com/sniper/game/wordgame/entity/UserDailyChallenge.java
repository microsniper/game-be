package com.sniper.game.wordgame.entity;

import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import com.sniper.game.wordgame.constant.enums.SourceEnum;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日挑战通关记录：每用户每天一行（uk_user_date），通关即记；
 * 无 level_num（每日挑战仅 1 关，行存在即代表当天已通关）。
 */
@Data
public class UserDailyChallenge {

    private Long id;

    private Long userId;

    private GameTypeEnum gameType;

    /** 挑战日期（服务器本地日） */
    private LocalDate challengeDate;

    /** 通关时省份快照（写入时取自 user.region_id，防改省刷榜） */
    private Integer regionId;

    /** 挑战开始时间（前端计时，通关时随上报写入） */
    private LocalDateTime startAt;

    /** 过关时间（服务器时刻） */
    private LocalDateTime clearAt;

    private SourceEnum source;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
