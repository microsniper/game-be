-- 无限模式套用每日挑战「分批规划」结构：按关卡区间配置 endless_challenge_wave_plan / endless_challenge_wave_plates
--
-- endless_challenge_wave_plan：前端 GameManager.generateLevel 按当前关号找第一个 level <= max 的区间，
--   区间内 batches=[{colors水果颜色数,layers层数}] 展开为逐层颜色数（口径与 daily_challenge_wave_plan 一致）；
--   区间内 weights={temp,click,block} 命中后覆盖 getPreferredRefreshColors 的刷色权重（口径同 daily_challenge_challenge_weights）；
--   区间内 unburyRatio/refillRatio 命中后覆盖 endless_layer_rules 同名字段（maxPlates/maxLayers/initialLoad 仍读 endless_layer_rules，不受本配置影响）。
--   某关卡未命中任何区间时，前端自动退回原整关曲线生成逻辑，不报错。
--
-- endless_challenge_wave_plates：按关卡区间配 batches=[{maxPlates,rectFirst,shapeFirst,stripFirst,shapeVariety}]，
--   口径与 daily_challenge_wave_plates 一致，未命中区间时退回 endless_layer_rules.maxPlates 铺满逻辑。
--
-- 区间断点对齐 endless_layer_rules 现有断点（30/80/999，先给3档起步，可随时改库调整，无需发版）。
-- 执行方式：mysql -u root -p game < migration_add_endless_challenge_wave_plan.sql

INSERT IGNORE INTO game_config (game_type, config_key, config_value, description) VALUES
(1, 'endless_challenge_wave_plan',
 '[{"max":30,"batches":[{"colors":4,"layers":2},{"colors":6,"layers":3},{"colors":8,"layers":3}],"weights":{"temp":5,"click":15,"block":80},"unburyRatio":0.65,"refillRatio":0.75},{"max":80,"batches":[{"colors":4,"layers":2},{"colors":7,"layers":3},{"colors":8,"layers":4}],"weights":{"temp":5,"click":15,"block":80},"unburyRatio":0.6,"refillRatio":0.7},{"max":999,"batches":[{"colors":4,"layers":2},{"colors":8,"layers":4},{"colors":9,"layers":6}],"weights":{"temp":5,"click":15,"block":80},"unburyRatio":0.6,"refillRatio":0.7}]',
 '无限模式挑战化批次计划（按关卡区间）：batches同每日挑战wave_plan口径；weights覆盖刷色权重；unburyRatio/refillRatio覆盖endless_layer_rules同名字段'),
(1, 'endless_challenge_wave_plates',
 '[{"max":30,"batches":[{"maxPlates":4,"rectFirst":2,"shapeFirst":2},{"maxPlates":5,"rectFirst":2,"shapeFirst":3,"stripFirst":1},{"maxPlates":6,"rectFirst":2,"shapeFirst":3,"stripFirst":1}]},{"max":80,"batches":[{"maxPlates":5,"rectFirst":2,"shapeFirst":2},{"maxPlates":7,"rectFirst":2,"shapeFirst":3,"stripFirst":1},{"maxPlates":9,"rectFirst":2,"shapeFirst":4,"stripFirst":1}]},{"max":999,"batches":[{"maxPlates":6,"rectFirst":2,"shapeFirst":2},{"maxPlates":10,"rectFirst":2,"shapeFirst":3,"stripFirst":1},{"maxPlates":20,"rectFirst":2,"shapeFirst":4,"stripFirst":2,"shapeVariety":2}]}]',
 '无限模式挑战化铺板参数（按关卡区间）：batches同每日挑战wave_plates口径，未命中区间时退回endless_layer_rules.maxPlates铺满逻辑');
