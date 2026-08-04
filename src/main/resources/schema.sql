CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `openid` VARCHAR(64) NOT NULL COMMENT '微信openid',
    `unionid` VARCHAR(64) DEFAULT NULL COMMENT '微信unionid',
    `source` TINYINT NOT NULL DEFAULT 1 COMMENT '1-微信小程序 2-抖音小程序',
    `game_type` TINYINT NOT NULL DEFAULT 1 COMMENT '1-采摘游戏',
    `nickname` VARCHAR(64) DEFAULT NULL COMMENT '昵称（允许重复，微信昵称不唯一）',
    `avatar_url` VARCHAR(512) DEFAULT NULL COMMENT '头像地址',
    `region_id` INT DEFAULT NULL COMMENT '地区ID，关联 region.id（数据关联，不加外键）',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_openid_game_type` (`openid`, `game_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `user_progress` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `game_type` TINYINT NOT NULL DEFAULT 1 COMMENT '1-采摘游戏',
    `level_num` INT NOT NULL DEFAULT 1 COMMENT '当前关卡数',
    `source` TINYINT NOT NULL DEFAULT 1 COMMENT '来源渠道 1-微信小程序 2-抖音小程序',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_game_type` (`user_id`, `game_type`),
    KEY `idx_game_type_level` (`game_type`, `level_num`)
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

CREATE TABLE IF NOT EXISTS `region` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '地区主键ID（user.region_id 存的就是它）',
    `name` VARCHAR(16) NOT NULL COMMENT '省级行政区名称',
    `code` VARCHAR(8) NOT NULL COMMENT '行政区划代码（GB/T 2260 两位）',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '展示排序，越小越靠前',
    `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用 1-启用 0-停用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地区字典表（省级行政区）';

-- 34 个省级行政区，显式指定 id（1~34），保证各环境主键一致
INSERT IGNORE INTO `region` (`id`, `code`, `name`, `sort_order`) VALUES
(1,'11','北京',1),(2,'12','天津',2),(3,'13','河北',3),(4,'14','山西',4),(5,'15','内蒙古',5),
(6,'21','辽宁',6),(7,'22','吉林',7),(8,'23','黑龙江',8),(9,'31','上海',9),(10,'32','江苏',10),
(11,'33','浙江',11),(12,'34','安徽',12),(13,'35','福建',13),(14,'36','江西',14),(15,'37','山东',15),
(16,'41','河南',16),(17,'42','湖北',17),(18,'43','湖南',18),(19,'44','广东',19),(20,'45','广西',20),
(21,'46','海南',21),(22,'50','重庆',22),(23,'51','四川',23),(24,'52','贵州',24),(25,'53','云南',25),
(26,'54','西藏',26),(27,'61','陕西',27),(28,'62','甘肃',28),(29,'63','青海',29),(30,'64','宁夏',30),
(31,'65','新疆',31),(32,'71','台湾',32),(33,'81','香港',33),(34,'82','澳门',34);

CREATE TABLE IF NOT EXISTS `user_daily_challenge` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `game_type` TINYINT NOT NULL DEFAULT 1 COMMENT '1-采摘游戏',
    `challenge_date` DATE NOT NULL COMMENT '挑战日期（服务器本地日）',
    `region_id` INT DEFAULT NULL COMMENT '通关时省份快照，关联 region.id（数据关联，不加外键）',
    `start_at` DATETIME DEFAULT NULL COMMENT '挑战开始时间（前端计时，通关时随上报写入）',
    `clear_at` DATETIME DEFAULT NULL COMMENT '过关时间（服务器时刻）',
    `source` TINYINT NOT NULL DEFAULT 1 COMMENT '来源渠道 1-微信小程序 2-抖音小程序',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（通关时刻）',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_date` (`user_id`, `game_type`, `challenge_date`),
    KEY `idx_region_rank` (`game_type`, `challenge_date`, `region_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日挑战通关记录表（每用户每天一行，通关即记）';
