package com.sniper.game.wordgame.entity;

import com.sniper.game.wordgame.constant.enums.FeedbackTypeEnum;
import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import com.sniper.game.wordgame.constant.enums.SourceEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户反馈：设置页"游戏反馈/意见反馈"入口提交，纯文字内容，不留联系方式。
 */
@Data
public class UserFeedback {

    private Long id;

    private Long userId;

    private GameTypeEnum gameType;

    private FeedbackTypeEnum feedbackType;

    private String content;

    private SourceEnum source;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
