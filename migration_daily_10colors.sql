-- 每日挑战第2关：批2/批3颜色数改10色（石榴、土豆加入，第二批开始每层10色全展示）
-- 覆盖 migration_daily_batch2_8colors.sql（8色不够，现10色全展示）
UPDATE game_config
SET config_value = '{"1":{"batches":[{"colors":4,"layers":1}]},"2":{"batches":[{"colors":4,"layers":2},{"colors":10,"layers":4},{"colors":10,"layers":4}]}}'
WHERE game_type = 1 AND config_key = 'daily_challenge_wave_plan';
