package com.sniper.game.wordgame.mapper;

import com.sniper.game.wordgame.entity.UserFeedback;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

public interface UserFeedbackMapper {

    int insert(UserFeedback record);

    /** 用户当天已提交的反馈条数（防刷限流用） */
    int countTodayByUser(@Param("userId") Long userId, @Param("today") LocalDate today);
}
