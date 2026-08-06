-- 每日挑战 wave_plates 补齐到 3 批（对齐 wave_plan 的 4色2层→8色4层→8色4层 三批节奏），并加入长条板（stripFirst）：
-- 批1（入门局）：不加长条板，维持简单
-- 批2/批3：各保底 1 块长条板，maxPlates 相应各加 1（5→6、6→7），避免长条板挤占方板/异形板保底名额
-- 新增 shapeVariety=2：每层最多出现 2 种板子形状（从方板2种+异形4种+长条1种共7种里随机抽2种，本层只铺这2种）。
--   注意：shapeFirst 配的 2/3/4 在有 shapeVariety=2 限制后，实际每层最多也就用得上 2 种，数字不会报错但意义打折，
--   如果不需要这个字段留着做备用，可以自行把 shapeFirst 也一并调成 2。
-- 执行方式：mysql -u root -p game < migration_daily_wave_plates_3batches.sql

UPDATE `game_config`
SET `config_value` = '{"1":{"batches":[{"maxPlates":4,"rectFirst":2,"shapeFirst":2,"stripFirst":0,"shapeVariety":2},{"maxPlates":6,"rectFirst":2,"shapeFirst":3,"stripFirst":1,"shapeVariety":2},{"maxPlates":7,"rectFirst":2,"shapeFirst":4,"stripFirst":1,"shapeVariety":2}]}}'
WHERE `game_type` = 1 AND `config_key` = 'daily_challenge_wave_plates';

-- 回退方式：把 config_value 换回不含 shapeVariety 的旧值即可（不限制形状种类）：
-- '{"1":{"batches":[{"maxPlates":4,"rectFirst":2,"shapeFirst":2,"stripFirst":0},{"maxPlates":6,"rectFirst":2,"shapeFirst":3,"stripFirst":1},{"maxPlates":7,"rectFirst":2,"shapeFirst":4,"stripFirst":1}]}}'
