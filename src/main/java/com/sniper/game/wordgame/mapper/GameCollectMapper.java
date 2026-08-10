package com.sniper.game.wordgame.mapper;

import com.sniper.game.wordgame.dto.CollectItemDto;

import java.util.List;

public interface GameCollectMapper {

    /** 收集品全量目录（仓库页/猫咪图标用，只读配置） */
    List<CollectItemDto> findAll();
}
