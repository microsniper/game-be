package com.sniper.game.wordgame.entity;

import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import com.sniper.game.wordgame.constant.enums.SourceEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProgress {

    private Long id;

    private Long userId;

    private GameTypeEnum gameType;

    private Integer levelNum;

    private SourceEnum source;

    private LocalDateTime updatedAt;
}
