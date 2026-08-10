-- 游戏难度配置初始数据（采摘游戏 game_type=1）
INSERT IGNORE INTO game_config (game_type, config_key, config_value, description) VALUES
(1, 'challenge_interval', '5', '每几关为挑战关'),
(1, 'normal_weights', '{"temp":20,"click":30,"block":60}', '普通关-颜色刷新权重'),
(1, 'challenge_weights', '{"temp":10,"click":20,"block":60}', '挑战关-颜色刷新权重'),
(1, 'box_capacity',
 '[{"max":6,"w3":100},{"max":11,"w3":85,"w4":15},{"max":16,"w3":65,"w4":35},{"max":21,"w3":50,"w4":35,"w5":15},{"max":27,"w3":40,"w4":35,"w5":25},{"max":35,"w3":25,"w4":35,"w5":30,"w6":10},{"max":45,"w3":15,"w4":35,"w5":35,"w6":15},{"max":999,"w3":10,"w4":30,"w5":35,"w6":25}]',
 '果篮孔数分布，按关卡区间');
