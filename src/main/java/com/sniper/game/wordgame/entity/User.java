package com.sniper.game.wordgame.entity;

import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import com.sniper.game.wordgame.constant.enums.SourceEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    private Long id;

    private String openid;

    private String unionid;

    private SourceEnum source;

    private GameTypeEnum gameType;

    private String nickname;

    private String avatarUrl;

    /** 地区ID，关联 region.id（数据关联，不加外键） */
    private Integer regionId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
