package com.sniper.game.wordgame.service;

import com.sniper.game.wordgame.constant.enums.FeedbackTypeEnum;
import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import com.sniper.game.wordgame.constant.enums.SourceEnum;
import com.sniper.game.wordgame.entity.User;
import com.sniper.game.wordgame.entity.UserFeedback;
import com.sniper.game.wordgame.exception.BusinessException;
import com.sniper.game.wordgame.mapper.UserFeedbackMapper;
import com.sniper.game.wordgame.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 用户反馈服务：设置页"游戏反馈/意见反馈"入口提交，每人每天限提交 5 条防刷。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private static final int DAILY_LIMIT = 5;

    private final UserFeedbackMapper userFeedbackMapper;

    private final UserMapper userMapper;

    public void submit(Long userId, FeedbackTypeEnum feedbackType, String content) {
        if (userId == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        int todayCount = userFeedbackMapper.countTodayByUser(userId, LocalDate.now());
        if (todayCount >= DAILY_LIMIT) {
            throw BusinessException.badRequest("今日反馈已达上限，请明天再来");
        }
        User user = userMapper.findById(userId);

        UserFeedback record = new UserFeedback();
        record.setUserId(userId);
        record.setGameType(GameTypeEnum.FRUIT_PICKING);
        record.setFeedbackType(feedbackType);
        record.setContent(content);
        record.setSource(user != null && user.getSource() != null ? user.getSource() : SourceEnum.WECHAT);
        userFeedbackMapper.insert(record);
        log.info("用户反馈提交: userId={}, feedbackType={}", userId, feedbackType);
    }
}
