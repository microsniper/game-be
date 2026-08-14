package com.sniper.game.wordgame.mapper;

import com.sniper.game.wordgame.dto.BackpackItemDto;
import com.sniper.game.wordgame.entity.UserBackpack;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserBackpackMapper {

    /** 当前用户拥有的收集品列表（仓库页展示用） */
    List<BackpackItemDto> findByUserId(@Param("userId") Long userId);

    UserBackpack findByUserIdAndCollectId(@Param("userId") Long userId, @Param("collectId") Long collectId);

    int insert(UserBackpack userBackpack);

    /** 累加拥有数量 */
    int incrementCount(@Param("userId") Long userId, @Param("collectId") Long collectId, @Param("amount") Integer amount);

    /** 先把该用户所有行清 is_current，再把目标 collect_id 置 1（同用户最多一条为当前展示项） */
    int clearCurrent(@Param("userId") Long userId);

    int setCurrent(@Param("userId") Long userId, @Param("collectId") Long collectId);
}
