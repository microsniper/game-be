-- 商城商品说明（卡片小字）。desc 为 MySQL 保留字，列名用 item_desc。
ALTER TABLE game_shop ADD COLUMN item_desc VARCHAR(64) DEFAULT NULL COMMENT '商品说明（商城卡片小字，可选）';

-- 特殊果说明回填（resource_code：5=彩虹果 6=炸弹果）
UPDATE game_shop s JOIN game_resource res ON s.item_id = res.id
SET s.item_desc = '可任意匹配果篮'
WHERE s.category = 1 AND res.resource_code = 5;

UPDATE game_shop s JOIN game_resource res ON s.item_id = res.id
SET s.item_desc = '可炸掉板子'
WHERE s.category = 1 AND res.resource_code = 6;
