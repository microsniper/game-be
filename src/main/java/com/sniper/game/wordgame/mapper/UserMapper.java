package com.sniper.game.wordgame.mapper;

import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import com.sniper.game.wordgame.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    User findByOpenidAndGameType(@Param("openid") String openid, @Param("gameType") GameTypeEnum gameType);

    User findById(@Param("id") Long id);

    int insert(User user);
}
