-- 砸板子道具价格迁移脚本
-- 执行方式：mysql -u root -p game < migration_add_smash_plate.sql
--
-- 内容：
--   1) game_config 表 tool_costs 行补齐 smashPlate 字段（砸板子价格，小太阳）
--   2) 历史环境若没有 tool_costs 行则整行插入（INSERT IGNORE 幂等）

-- 1) 已有 tool_costs 行：合入 smashPlate（JSON_MERGE_PATCH 幂等，重复执行无副作用）
UPDATE `game_config`
SET `config_value` = JSON_MERGE_PATCH(`config_value`, '{"smashPlate":20}')
WHERE `game_type` = 1 AND `config_key` = 'tool_costs';

-- 2) 没有 tool_costs 行：整行兜底插入
INSERT IGNORE INTO `game_config` (`game_type`, `config_key`, `config_value`, `description`) VALUES
(1, 'tool_costs', '{"addBasket":20,"clearTray":20,"smashPlate":20}', '道具价格（小太阳）：加果篮/清空果盘/砸板子');
