package com.sniper.game.wordgame.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GameConfig {

    private Long id;

    private Integer gameType;

    private String configKey;

    private String configValue;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
