-- 每日挑战记录补充时间字段：挑战开始时间（前端计时，通关随上报写入）+ 过关时间（服务器时刻）
-- 执行方式：mysql -u root -p game < migration_add_daily_challenge_time.sql
-- 注意：ALTER 不幂等，报 Duplicate column name 即已执行过，跳过即可

ALTER TABLE `user_daily_challenge`
    ADD COLUMN `start_at` DATETIME DEFAULT NULL COMMENT '挑战开始时间（前端计时，通关时随上报写入）' AFTER `region_id`,
    ADD COLUMN `clear_at` DATETIME DEFAULT NULL COMMENT '过关时间（服务器时刻）' AFTER `start_at`;
