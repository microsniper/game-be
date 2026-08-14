package com.sniper.game.wordgame.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserBackpack {

    private Long id;

    private Long userId;

    private Long collectId;

    private Integer count;

    private Boolean isCurrent;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
