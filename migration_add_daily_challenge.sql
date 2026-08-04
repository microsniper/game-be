-- 每日挑战通关记录表（每用户每天一行，通关即记；省份榜按 challenge_date 分组统计）
CREATE TABLE IF NOT EXISTS `user_daily_challenge` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `game_type` TINYINT NOT NULL DEFAULT 1 COMMENT '1-采摘游戏',
    `challenge_date` DATE NOT NULL COMMENT '挑战日期（服务器本地日）',
    `region_id` INT DEFAULT NULL COMMENT '通关时省份快照，关联 region.id（数据关联，不加外键）',
    `source` TINYINT NOT NULL DEFAULT 1 COMMENT '来源渠道 1-微信小程序 2-抖音小程序',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（通关时刻）',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_date` (`user_id`, `game_type`, `challenge_date`),
    KEY `idx_region_rank` (`game_type`, `challenge_date`, `region_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日挑战通关记录表（每用户每天一行，通关即记）';
