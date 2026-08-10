-- 收集表改造 + 商城表
-- 执行方式：mysql -u root -p game < migration_shop_and_collect.sql
--
-- 设计要点：
-- 1. game_skin 改名 game_collect（收集表）：原表无数据，直接改结构；
--    skin_code → collect_code；删 scope（收集品拥有是全局的，不按模式分）；加 group_code 分组（动物/豪车/房子/公仔）
-- 2. 掉落归属仍由 game_reward_config 的 mode 池子表达（item_type=2 关联 game_collect.id），收集表本身不存模式
-- 3. game_shop 商城配置表：category=1 道具关联 game_resource.id；category=2 收集关联 game_collect.id；价格表内配置
-- 4. 购买发放仍走前端本地账（totalCoins/PropStore/未来 CollectStore），后端只下发目录

-- ===== 1. 皮肤表 → 收集表 =====
RENAME TABLE game_skin TO game_collect;

ALTER TABLE game_collect
    CHANGE COLUMN skin_code collect_code VARCHAR(32) NOT NULL COMMENT '收集品编码（cat/dog/car/doll...）',
    DROP COLUMN scope,
    ADD COLUMN group_code VARCHAR(32) NOT NULL DEFAULT '' COMMENT '分组：animal=动物 car=车辆 house=建筑 doll=公仔',
    DROP INDEX uk_scope_code,
    ADD UNIQUE KEY uk_collect_code (collect_code);

-- ===== 2. 商城表 =====
CREATE TABLE IF NOT EXISTS game_shop (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category TINYINT NOT NULL COMMENT '分类：1=道具 2=收集',
    item_id BIGINT NOT NULL COMMENT 'category=1 关联 game_resource.id；category=2 关联 game_collect.id（数据关联，不加外键）',
    price INT NOT NULL COMMENT '价格（金币）',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '同分类内排序，升序',
    enabled TINYINT NOT NULL DEFAULT 1 COMMENT '1=上架 0=下架',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_category (category, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城配置表（资源关联 game_resource/game_collect，价格表内配置）';

-- ===== 3. 种子：5 条道具行（resource_id 按 resource_code 子查询关联，不写死 id） =====
INSERT INTO game_shop (category, item_id, price, sort_order)
SELECT 1, id, 1000, 1 FROM game_resource WHERE resource_code = 5
UNION ALL
SELECT 1, id, 1000, 2 FROM game_resource WHERE resource_code = 6
UNION ALL
SELECT 1, id, 1000, 3 FROM game_resource WHERE resource_code = 2
UNION ALL
SELECT 1, id, 1000, 4 FROM game_resource WHERE resource_code = 3
UNION ALL
SELECT 1, id, 1500, 5 FROM game_resource WHERE resource_code = 4;
