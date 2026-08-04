-- 每日挑战全套难度配置（随时改库生效，下次冷启动拉取；INSERT IGNORE 幂等，已存在的自定义值不被覆盖）
-- 执行方式：mysql -u root -p game < migration_add_daily_challenge_tuning.sql

INSERT IGNORE INTO `game_config` (`game_type`, `config_key`, `config_value`, `description`) VALUES
(1, 'daily_challenge_wave_plan', '{"batches":[{"colors":4,"layers":2},{"colors":6,"layers":4},{"colors":8,"layers":4}]}', '每日挑战批次计划：colors=该批水果颜色数，layers=该批层数（总层数=各批之和，难度逐批递增）'),
(1, 'daily_challenge_wave_plates', '{"batches":[{"maxPlates":5,"rectFirst":2,"shapeFirst":3}]}', '每日挑战每层板子数（按批）：maxPlates缺省=铺满；rectFirst/shapeFirst=方板/异形保底块数'),
(1, 'daily_challenge_box_capacity', '{"w3":10,"w4":20,"w5":30,"w6":40}', '每日挑战果篮孔数分布权重：w3~w6为3~6孔占比'),
(1, 'daily_challenge_challenge_weights', '{"temp":5,"click":15,"block":80}', '每日挑战果篮刷新颜色权重：temp暂存区/click可点/block被埋'),
(1, 'daily_challenge_layer_rules', '{"unburyRatio":0.6,"refillRatio":0.7}', '每日挑战层流规则：unburyRatio遮挡翻彩阈值/refillRatio补层阈值');
