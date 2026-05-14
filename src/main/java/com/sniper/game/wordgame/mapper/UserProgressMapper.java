package com.sniper.game.wordgame.mapper;

import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import com.sniper.game.wordgame.entity.UserProgress;
import org.apache.ibatis.annotations.Param;

public interface UserProgressMapper {

    UserProgress findByUserIdAndGameType(@Param("userId") Long userId, @Param("gameType") GameTypeEnum gameType);

    int insert(UserProgress userProgress);

    int updateLevelNum(@Param("userId") Long userId,
                       @Param("gameType") GameTypeEnum gameType,
                       @Param("levelNum") Integer levelNum);
}
