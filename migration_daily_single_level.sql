-- 每日挑战砍成单关：原第 2 关内容变成唯一的挑战关（key "1"）
-- wave_plan：key "1" 换成原 "2" 的三批渐难计划（4色2层 → 8色4层 → 8色4层）
UPDATE game_config
SET config_value = '{"1":{"batches":[{"colors":4,"layers":2},{"colors":8,"layers":4},{"colors":8,"layers":4}]}}'
WHERE game_type = 1 AND config_key = 'daily_challenge_wave_plan';

-- wave_plates：key "1" 换成原 "2" 的板子配置
UPDATE game_config
SET config_value = '{"1":{"batches":[{"maxPlates":5,"rectFirst":2,"shapeFirst":3}]}}'
WHERE game_type = 1 AND config_key = 'daily_challenge_wave_plates';

-- 说明：前端 TOTAL_LEVELS 同步改为 1，只会读 key "1"；
-- 回退方式：把 config_value 换回含 "1"(4色1层傻瓜局)+"2" 两个 key 的旧值即可。
