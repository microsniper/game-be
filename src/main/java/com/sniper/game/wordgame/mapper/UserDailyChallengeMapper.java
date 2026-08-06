package com.sniper.game.wordgame.mapper;

import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import com.sniper.game.wordgame.dto.DailyRankResponse;
import com.sniper.game.wordgame.entity.UserDailyChallenge;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface UserDailyChallengeMapper {

    UserDailyChallenge findByUserAndDate(@Param("userId") Long userId,
                                         @Param("gameType") GameTypeEnum gameType,
                                         @Param("challengeDate") LocalDate challengeDate);

    int insert(UserDailyChallenge record);

    /**
     * 重复挑战更快时刷新起止时间：仅当新耗时短于已存耗时才更新（比较在 SQL 内完成，避免并发覆盖）。
     * 表内一人一天一行，存的始终是当天最快那次的 start_at / clear_at，时长按需相减得出。
     * 起止时间均为前端上报的计时时间戳（同一设备的钟），保证相减口径与前端「本次用时」一致。
     *
     * @return 实际更新行数，0 表示没更快、未更新
     */
    int updateIfFaster(@Param("userId") Long userId,
                       @Param("gameType") GameTypeEnum gameType,
                       @Param("challengeDate") LocalDate challengeDate,
                       @Param("startAt") LocalDateTime startAt,
                       @Param("clearAt") LocalDateTime clearAt);

    /** 当天最快耗时（秒）；无记录或起止时间缺失返回 null */
    Integer findBestSeconds(@Param("userId") Long userId,
                            @Param("gameType") GameTypeEnum gameType,
                            @Param("challengeDate") LocalDate challengeDate);

    /** 省份榜：当天各省通关人数（人数降序、省份 id 升序稳定输出），DENSE_RANK 在 Java 层计算 */
    List<DailyRankResponse.RankItem> countByRegionGroup(@Param("gameType") GameTypeEnum gameType,
                                                        @Param("challengeDate") LocalDate challengeDate);
}
