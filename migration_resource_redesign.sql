-- 资源表重构：类型编码从 sign_in_reward 迁到 game_resource
-- 设计意图：资源是什么类型（sun/smash/.../rainbow-bomb）是资源本身的属性，
-- 签到表只管「第几天发哪个资源、发几个」，类型经 resource_id 关联取得。
--
-- 执行顺序严格：先改代码部署（新 SQL 走 res.resource_code），再跑本迁移；
-- 或确认回填无误后一次性执行。第 3 步 DROP 不可逆。

-- ===== 执行前校验：同一个资源是否被多个不同类型引用（有结果则不能直接回填）=====
-- SELECT r.resource_id, COUNT(DISTINCT r.reward_type) AS type_cnt
-- FROM sign_in_reward r
-- GROUP BY r.resource_id
-- HAVING type_cnt > 1;

-- 1. game_resource 加类型编码列（唯一、可空兼容存量未编码资源）
ALTER TABLE game_resource
    ADD COLUMN resource_code VARCHAR(32) NULL COMMENT '资源类型编码（ResourceCodeTypeEnum 的 code）',
    ADD UNIQUE KEY uk_resource_code (resource_code);

-- 2. 回填：把 sign_in_reward.reward_type 迁到对应资源上
UPDATE game_resource res
    JOIN sign_in_reward r ON r.resource_id = res.id
SET res.resource_code = r.reward_type
WHERE res.resource_code IS NULL;

-- ===== 回填校验：所有签到行都能经资源表拿到类型（有结果说明回填遗漏）=====
-- SELECT r.id, r.day_num
-- FROM sign_in_reward r
--          JOIN game_resource res ON r.resource_id = res.id
-- WHERE res.resource_code IS NULL;

-- 3. 确认回填无误后删列
ALTER TABLE sign_in_reward DROP COLUMN reward_type;
