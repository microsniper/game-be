-- 用户收集品背包表：把收集品拥有/当前展示状态从前端本地存储（CollectStore）迁移到后端。
-- 收集功能尚未上线，无需考虑老数据迁移，全新建表即可。
-- 执行方式：mysql -u root -p game < migration_add_user_backpack.sql

CREATE TABLE IF NOT EXISTS `user_backpack` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `collect_id` BIGINT NOT NULL COMMENT '关联 game_collect.id',
    `count` INT NOT NULL DEFAULT 1 COMMENT '拥有数量，支持重复拥有累加',
    `is_current` TINYINT NOT NULL DEFAULT 0 COMMENT '是否为当前展示项，同用户最多一条为1',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次获得时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_collect` (`user_id`, `collect_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收集品背包表';
