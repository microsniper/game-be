-- 每日挑战批次计划结构升级 v2：按关号分组（第 1 关单层 4 板傻瓜局，第 2 关 3 批 10 层渐难）
-- UPDATE 覆盖 v1 旧结构值；INSERT IGNORE 兜底（行不存在时插入）
-- 执行方式：mysql -u root -p game < migration_add_daily_challenge_tuning_v2.sql

UPDATE `game_config`
SET `config_value` = '{"1":{"batches":[{"colors":4,"layers":1}]},"2":{"batches":[{"colors":4,"layers":2},{"colors":6,"layers":4},{"colors":8,"layers":4}]}}'
WHERE `game_type` = 1 AND `config_key` = 'daily_challenge_wave_plan';

UPDATE `game_config`
SET `config_value` = '{"1":{"batches":[{"maxPlates":4,"rectFirst":2,"shapeFirst":2}]},"2":{"batches":[{"maxPlates":5,"rectFirst":2,"shapeFirst":3}]}}'
WHERE `game_type` = 1 AND `config_key` = 'daily_challenge_wave_plates';

INSERT IGNORE INTO `game_config` (`game_type`, `config_key`, `config_value`, `description`) VALUES
(1, 'daily_challenge_wave_plan', '{"1":{"batches":[{"colors":4,"layers":1}]},"2":{"batches":[{"colors":4,"layers":2},{"colors":6,"layers":4},{"colors":8,"layers":4}]}}', '每日挑战批次计划（按关号分组）：关号.batches=[{colors水果颜色数,layers层数}]，key直接是关号不要再包levels层'),
(1, 'daily_challenge_wave_plates', '{"1":{"batches":[{"maxPlates":4,"rectFirst":2,"shapeFirst":2}]},"2":{"batches":[{"maxPlates":5,"rectFirst":2,"shapeFirst":3}]}}', '每日挑战每层板子数（按关号分组再按批）：关号.batches=[{maxPlates缺省=铺满,rectFirst,shapeFirst}]，key直接是关号不要再包levels层');
