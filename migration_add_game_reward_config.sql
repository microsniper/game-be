-- 过关奖励配置：每日挑战/无限模式通用（区分 mode），配置放数据库，发放仍走前端本地记账
-- 执行方式：mysql -u root -p game < migration_add_game_reward_config.sql
--
-- 设计要点：
-- 1. game_skin：皮肤配置表（本次新建，暂无数据写入，供未来皮肤功能使用）
-- 2. game_reward_config：过关奖励配置，item_type + item_id 多态关联（不加外键，与项目现有风格一致）
--    - item_type=1(prop) 时 item_id 关联 game_resource.id（含金币 sun）
--    - item_type=2(skin) 时 item_id 关联 game_skin.id
--    - mode + stage 区分模式与阶段：stage=1 通常只有一条（金币），stage=2 多条按 weight 加权随机抽一条
-- 3. mode / item_type / scope 均为 TINYINT 枚举 code（对应 Java 侧 GameRewardModeEnum / ItemTypeEnum）：
--    mode: 1=dailyChallenge(每日挑战) 2=endlessChallenge(无限模式)
--    item_type: 1=prop(道具，含金币) 2=skin(皮肤)
--
-- 范围说明：本次奖励发放（金币/道具写入）仍走前端 PropStore/totalCoins 本地记账，
-- 后端只提供配置查询与随机抽签，不记录用户领取状态、不落用户侧数据。

CREATE TABLE IF NOT EXISTS game_skin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '皮肤名称',
    url VARCHAR(500) NOT NULL COMMENT 'OSS CDN 地址（皮肤预览图/贴图）',
    scope TINYINT NOT NULL COMMENT '适用模式：1=dailyChallenge(每日挑战) 2=endlessChallenge(无限模式)',
    skin_code VARCHAR(32) NOT NULL COMMENT '皮肤编码，同 scope 内唯一',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_scope_code (scope, skin_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='皮肤配置表（本次仅建表，暂无数据）';

CREATE TABLE IF NOT EXISTS game_reward_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    mode TINYINT NOT NULL COMMENT '游戏模式：1=dailyChallenge(每日挑战) 2=endlessChallenge(无限模式)',
    stage INT NOT NULL COMMENT '领取阶段：1=第一次点领取奖励(金币)，2=第二次(道具/皮肤按权重随机)',
    item_type TINYINT NOT NULL COMMENT '物品类型：1=prop(道具，含金币) 2=skin(皮肤)',
    item_id BIGINT NOT NULL COMMENT 'item_type=1(prop) 时关联 game_resource.id；item_type=2(skin) 时关联 game_skin.id（数据关联，不加外键）',
    amount INT NOT NULL DEFAULT 1 COMMENT '数量',
    weight INT NOT NULL DEFAULT 1 COMMENT '同一 mode+stage 内多条候选的随机权重；stage=1 通常只有一条，权重无意义',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_mode_stage (mode, stage)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='过关奖励配置（按模式与阶段区分，权重随机抽取）';
