-- 用户反馈表：设置页"游戏反馈/意见反馈"入口提交，纯文字内容，不留联系方式
CREATE TABLE IF NOT EXISTS `user_feedback` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `game_type` TINYINT NOT NULL DEFAULT 1 COMMENT '1-采摘游戏',
    `feedback_type` TINYINT NOT NULL COMMENT '反馈类型 1-游戏反馈(bug/卡顿等) 2-意见反馈(建议)',
    `content` VARCHAR(500) NOT NULL COMMENT '反馈内容',
    `source` TINYINT NOT NULL DEFAULT 1 COMMENT '来源渠道 1-微信小程序 2-抖音小程序',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_created` (`user_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户反馈表（游戏反馈/意见反馈）';
