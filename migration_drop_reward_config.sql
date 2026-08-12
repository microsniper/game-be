-- 过关奖励规则已硬编码进后端 RewardService（每日挑战 3 stage / 无限模式倍数关抽奖），
-- 抽奖池复用 game_resource / game_collect 目录表，game_reward_config 配置表废弃。
DROP TABLE IF EXISTS game_reward_config;
