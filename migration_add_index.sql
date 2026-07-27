-- 排行榜查询优化：为 user_progress 添加 (game_type, level_num) 索引
-- 执行方式：mysql -u root -p game_db < migration_add_index.sql

CREATE INDEX idx_game_type_level ON user_progress (game_type, level_num);
