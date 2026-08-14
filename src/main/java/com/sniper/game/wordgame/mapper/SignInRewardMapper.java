package com.sniper.game.wordgame.mapper;

import com.sniper.game.wordgame.dto.SignInRewardItem;

import java.util.List;

public interface SignInRewardMapper {

    /** 7 天奖励配置（item_type=1 JOIN 资源表、=2 JOIN 收集表取图 URL），按 day_num 升序 */
    List<SignInRewardItem> findAllWithResource();
}
