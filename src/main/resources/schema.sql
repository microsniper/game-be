CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `openid` VARCHAR(64) NOT NULL COMMENT '微信openid',
    `unionid` VARCHAR(64) DEFAULT NULL COMMENT '微信unionid',
    `source` TINYINT NOT NULL DEFAULT 1 COMMENT '1-微信小程序 2-抖音小程序',
    `game_type` TINYINT NOT NULL DEFAULT 1 COMMENT '1-螺丝游戏',
    `nickname` VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    `avatar_url` VARCHAR(256) DEFAULT NULL COMMENT '头像地址',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_openid_game_type` (`openid`, `game_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `user_progress` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `game_type` TINYINT NOT NULL DEFAULT 1 COMMENT '1-螺丝游戏',
    `level_num` INT NOT NULL DEFAULT 1 COMMENT '当前关卡数',
    `source` TINYINT NOT NULL DEFAULT 1 COMMENT '来源渠道 1-微信小程序 2-抖音小程序',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_game_type` (`user_id`, `game_type`),
    CONSTRAINT `fk_user_progress_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户进度表';
