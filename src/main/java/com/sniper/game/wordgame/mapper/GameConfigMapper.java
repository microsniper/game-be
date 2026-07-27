package com.sniper.game.wordgame.mapper;

import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import com.sniper.game.wordgame.entity.GameConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GameConfigMapper {

    List<GameConfig> findByGameType(@Param("gameType") GameTypeEnum gameType);

    GameConfig findByConfigKey(@Param("configKey") String configKey);
}
