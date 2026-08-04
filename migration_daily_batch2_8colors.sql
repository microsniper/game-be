-- 每日挑战第2关：批2颜色数 6→8（第二批开始每层8色全展示，增加难度）
-- 批1保持4色2层（简单开局），批2/批3均8色
UPDATE game_config
SET config_value = '{"1":{"batches":[{"colors":4,"layers":1}]},"2":{"batches":[{"colors":4,"layers":2},{"colors":8,"layers":4},{"colors":8,"layers":4}]}}'
WHERE game_type = 1 AND config_key = 'daily_challenge_wave_plan';
