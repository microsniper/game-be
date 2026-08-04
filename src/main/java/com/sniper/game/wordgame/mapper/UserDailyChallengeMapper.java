package com.sniper.game.wordgame.mapper;

import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import com.sniper.game.wordgame.dto.DailyRankResponse;
import com.sniper.game.wordgame.entity.UserDailyChallenge;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface UserDailyChallengeMapper {

    UserDailyChallenge findByUserAndDate(@Param("userId") Long userId,
                                         @Param("gameType") GameTypeEnum gameType,
                                         @Param("challengeDate") LocalDate challengeDate);

    int insert(UserDailyChallenge record);

    /** 省份榜：当天各省通关人数（人数降序、省份 id 升序稳定输出），DENSE_RANK 在 Java 层计算 */
    List<DailyRankResponse.RankItem> countByRegionGroup(@Param("gameType") GameTypeEnum gameType,
                                                        @Param("challengeDate") LocalDate challengeDate);
}
