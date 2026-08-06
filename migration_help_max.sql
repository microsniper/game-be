-- 求助好友每日上限配置化：help_max（按模式分开，两模式各自计数互不影响）
-- dailyChallenge=每日挑战，endlessChallenge=无限模式；缺省时代码兜底 4
INSERT INTO game_config (game_type, config_key, config_value, description)
VALUES (1, 'help_max', '{"dailyChallenge":4,"endlessChallenge":4}', '求助好友每日上限（按模式）')
ON DUPLICATE KEY UPDATE config_value = VALUES(config_value);
