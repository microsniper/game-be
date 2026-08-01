-- 地区选择功能迁移脚本
-- 执行方式：mysql -u root -p game < migration_add_region.sql
--
-- 内容：
--   1) 新建 region 地区字典表并灌入 34 个省级行政区
--   2) user 表增加 region_id 列（存 region.id，数据关联不加外键）
--   3) 去掉 user_progress 的外键约束（数据靠 user_id 关联即可，外键徒增迁移/删数据成本；
--      user_id 已是 uk_user_game_type 的最左列，删约束不影响查询，无多余索引残留）

-- 1) 地区字典表
CREATE TABLE IF NOT EXISTS `region` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '地区主键ID（user.region_id 存的就是它）',
    `name` VARCHAR(16) NOT NULL COMMENT '省级行政区名称',
    `code` VARCHAR(8) NOT NULL COMMENT '行政区划代码（GB/T 2260 两位）',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '展示排序，越小越靠前',
    `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用 1-启用 0-停用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地区字典表（省级行政区）';

INSERT IGNORE INTO `region` (`id`, `code`, `name`, `sort_order`) VALUES
(1,'11','北京',1),(2,'12','天津',2),(3,'13','河北',3),(4,'14','山西',4),(5,'15','内蒙古',5),
(6,'21','辽宁',6),(7,'22','吉林',7),(8,'23','黑龙江',8),(9,'31','上海',9),(10,'32','江苏',10),
(11,'33','浙江',11),(12,'34','安徽',12),(13,'35','福建',13),(14,'36','江西',14),(15,'37','山东',15),
(16,'41','河南',16),(17,'42','湖北',17),(18,'43','湖南',18),(19,'44','广东',19),(20,'45','广西',20),
(21,'46','海南',21),(22,'50','重庆',22),(23,'51','四川',23),(24,'52','贵州',24),(25,'53','云南',25),
(26,'54','西藏',26),(27,'61','陕西',27),(28,'62','甘肃',28),(29,'63','青海',29),(30,'64','宁夏',30),
(31,'65','新疆',31),(32,'71','台湾',32),(33,'81','香港',33),(34,'82','澳门',34);

-- 2) user 表增加地区 id 列（可空：老用户没选过）
ALTER TABLE `user` ADD COLUMN `region_id` INT DEFAULT NULL COMMENT '地区ID，关联 region.id（数据关联，不加外键）' AFTER `avatar_url`;

-- 3) 去掉 user_progress 外键约束（改数据关联）
ALTER TABLE `user_progress` DROP FOREIGN KEY `fk_user_progress_user_id`;
