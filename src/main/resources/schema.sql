CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `openid` VARCHAR(64) NOT NULL COMMENT '微信openid',
    `unionid` VARCHAR(64) DEFAULT NULL COMMENT '微信unionid',
    `source` TINYINT NOT NULL DEFAULT 1 COMMENT '1-微信小程序 2-抖音小程序',
    `game_type` TINYINT NOT NULL DEFAULT 1 COMMENT '1-螺丝游戏',
    `nickname` VARCHAR(64) DEFAULT NULL COMMENT '昵称（允许重复，微信昵称不唯一）',
    `avatar_url` VARCHAR(512) DEFAULT NULL COMMENT '头像地址',
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
    KEY `idx_game_type_level` (`game_type`, `level_num`),
    CONSTRAINT `fk_user_progress_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户进度表';

CREATE TABLE IF NOT EXISTS `game_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `game_type` TINYINT NOT NULL COMMENT '游戏类型 1=采摘游戏',
    `config_key` VARCHAR(64) NOT NULL COMMENT '配置键名',
    `config_value` TEXT NOT NULL COMMENT '配置值',
    `description` VARCHAR(256) DEFAULT NULL COMMENT '说明',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_game_type_key` (`game_type`, `config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游戏配置表';
