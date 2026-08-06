package com.sniper.game.wordgame.mapper;

import com.sniper.game.wordgame.entity.GameResource;

import java.util.List;

public interface GameResourceMapper {

    /** 登记上传的资源，回填自增 id */
    int insert(GameResource resource);

    /** 查所有登记了类型编码的资源（resource_code 非空），供 /api/game/resources 下发 */
    List<GameResource> findAllWithCode();
}
