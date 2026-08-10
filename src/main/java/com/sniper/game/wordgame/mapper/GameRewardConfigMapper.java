package com.sniper.game.wordgame.mapper;

import com.sniper.game.wordgame.constant.enums.GameRewardModeEnum;
import com.sniper.game.wordgame.dto.RewardCandidate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GameRewardConfigMapper {

    /**
     * 阶段 1 固定奖励（通常只有一条，直接下发）。
     * JOIN game_resource/game_collect 取图 URL 与编码；无配置返回 null。
     */
    RewardCandidate findFixedReward(@Param("mode") GameRewardModeEnum mode, @Param("stage") Integer stage);

    /** 阶段 2 候选池（含 weight，供 Java 层加权随机抽取） */
    List<RewardCandidate> findCandidates(@Param("mode") GameRewardModeEnum mode, @Param("stage") Integer stage);
}
