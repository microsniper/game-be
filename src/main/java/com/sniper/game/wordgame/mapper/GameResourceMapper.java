package com.sniper.game.wordgame.mapper;

import com.sniper.game.wordgame.entity.GameResource;

public interface GameResourceMapper {

    /** 登记上传的资源，回填自增 id */
    int insert(GameResource resource);
}
