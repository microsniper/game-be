-- ResourceCodeTypeEnum 重构：resource_code 由字符串 code（sun/smash/...）改为数字 code
--   1=金币 2=加果盘 3=清空果盘 4=加果篮 5=彩虹果 6=炸弹果 7=彩虹果+炸弹果
-- 数据已由人工回填为数字（SUN→COIN、SMASH→ADD_TRAY），本迁移只改列类型。
-- 执行方式：mysql -u root -p game < migration_resource_code_to_int.sql
--
-- 前置条件：game_resource.resource_code 所有非空行已是 1~7 的数字字符串，
-- 否则 MODIFY 转 TINYINT 会报 Invalid number 或截断为 0。
-- 校验：SELECT id, name, resource_code FROM game_resource WHERE resource_code IS NOT NULL;

ALTER TABLE game_resource
    MODIFY COLUMN resource_code TINYINT NULL COMMENT '资源类型编码（ResourceCodeTypeEnum 数字 code：1=金币 2=加果盘 3=清空果盘 4=加果篮 5=彩虹果 6=炸弹果 7=彩虹果+炸弹果）';
