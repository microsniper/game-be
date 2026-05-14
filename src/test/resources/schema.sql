CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `openid` VARCHAR(64) NOT NULL,
    `unionid` VARCHAR(64),
    `source` TINYINT NOT NULL DEFAULT 1,
    `game_type` TINYINT NOT NULL DEFAULT 1,
    `nickname` VARCHAR(64),
    `avatar_url` VARCHAR(256),
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (`openid`, `game_type`)
);

CREATE TABLE IF NOT EXISTS `user_progress` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `game_type` TINYINT NOT NULL DEFAULT 1,
    `level_num` INT NOT NULL DEFAULT 1,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (`user_id`, `game_type`),
    CONSTRAINT `fk_user_progress_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);
