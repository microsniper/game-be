-- 每日签到功能：通用资源表 + 签到奖励配置表
-- 图片经后端上传接口存阿里云 OSS，url 为 CDN 地址；签到奖励通过 resource_id 关联资源表
-- 签到状态（已签天数/今日是否已领）全部存前端 localStorage，后端无用户签到状态

-- 1. 通用资源表（上传到 OSS 的图片登记在这）
CREATE TABLE IF NOT EXISTS game_resource (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '资源说明，如：签到第3天奖励图',
    url VARCHAR(500) NOT NULL COMMENT 'OSS CDN 地址',
    type VARCHAR(20) NOT NULL DEFAULT 'image' COMMENT '资源类型',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通用资源表';

-- 2. 签到奖励配置表（7 天，关联资源表；改奖励只需改这张表）
CREATE TABLE IF NOT EXISTS sign_in_reward (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    day_num INT NOT NULL COMMENT '签到第几天（1-7）',
    resource_id BIGINT NOT NULL COMMENT '关联 game_resource.id（奖励图片）',
    reward_type VARCHAR(20) NOT NULL COMMENT '奖励类型：sun(小太阳)/smash(砸板子)/clear(清空果盘)/add(加果篮)/rainbow(彩虹果)/bomb(炸弹果)/rainbow-bomb(彩虹果+炸弹果)',
    amount INT NOT NULL DEFAULT 1 COMMENT '数量',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_day (day_num)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='七日签到奖励配置';
