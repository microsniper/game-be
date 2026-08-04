-- 每日挑战第2关：批2/批3颜色数改14色（葡萄、香蕉、西瓜、樱桃加入，第二批开始每层14色全展示）
-- 覆盖 migration_daily_10colors.sql（10色不够，现14色全展示）
UPDATE game_config
SET config_value = '{"1":{"batches":[{"colors":4,"layers":1}]},"2":{"batches":[{"colors":4,"layers":2},{"colors":14,"layers":4},{"colors":14,"layers":4}]}}'
WHERE game_type = 1 AND config_key = 'daily_challenge_wave_plan';
