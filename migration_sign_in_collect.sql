-- 七日签到支持配置收集品玩偶奖励：resource_id 改名 item_id + 新增 item_type 区分道具/收集
-- 执行方式：mysql -u root -p game < migration_sign_in_collect.sql
--
-- 设计要点：
-- 1. item_type=1(prop) 时 item_id 关联 game_resource.id（含金币，原有 7 行都是这个类型，行为不变）；
--    item_type=2(collect) 时 item_id 关联 game_collect.id（新增能力，配置某天发收集品玩偶）
-- 2. 与 game_shop 的 category+item_id 多态关联同款设计，item_type 对应 Java 侧 ItemTypeEnum
-- 3. 现有 7 行数据默认 item_type=1，不影响现网配置

ALTER TABLE sign_in_reward
    CHANGE COLUMN resource_id item_id BIGINT NOT NULL COMMENT 'item_type=1 关联 game_resource.id；item_type=2 关联 game_collect.id',
    ADD COLUMN item_type TINYINT NOT NULL DEFAULT 1 COMMENT '奖励类型：1=道具(含金币) 2=收集品，对应 ItemTypeEnum' AFTER day_num;
