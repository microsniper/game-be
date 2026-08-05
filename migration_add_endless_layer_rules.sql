-- 无限模式层流规则（按关卡区间配置化）
-- 前端 EndlessDriver 按当前关号找第一个 level <= max 的区间，缺字段回落默认值
-- （maxPlates=40 / maxLayers=10 / initialLoad=2 / refillRatio=0.7 / unburyRatio=0.6）
-- 第 1 关为前端写死的新手局（1 层 3 板），不走本配置
INSERT IGNORE INTO game_config (game_type, config_key, config_value, description) VALUES
(1, 'endless_layer_rules',
 '[{"max":10,"maxPlates":40,"maxLayers":10,"initialLoad":2,"refillRatio":0.75,"unburyRatio":0.65},{"max":30,"maxPlates":40,"maxLayers":10,"initialLoad":2,"refillRatio":0.7,"unburyRatio":0.6},{"max":999,"maxPlates":40,"maxLayers":12,"initialLoad":3,"refillRatio":0.6,"unburyRatio":0.5}]',
 '无限模式层流规则（按关卡区间）：max=关卡上界；maxPlates单层板子数上限/maxLayers一关最多层数/initialLoad开局启用层数/refillRatio补层阈值比例/unburyRatio灰板翻彩遮挡阈值比例');
