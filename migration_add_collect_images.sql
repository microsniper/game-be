-- game_collect 补充灰/彩两态图 + 新人赠礼标记
-- gray_url/color_url 用于猫咪进度图标等灰彩两态展示；is_starter_gift 标记新用户默认赠送的收集品（全表应只有一行为1，代码不强制校验，配置时自行保证）
ALTER TABLE game_collect
    ADD COLUMN gray_url VARCHAR(500) NOT NULL DEFAULT '' COMMENT '灰色态图 OSS 地址（未点亮/仓库未拥有展示）',
    ADD COLUMN color_url VARCHAR(500) NOT NULL DEFAULT '' COMMENT '彩色态图 OSS 地址（点亮/已拥有展示）',
    ADD COLUMN is_starter_gift TINYINT NOT NULL DEFAULT 0 COMMENT '1=新用户默认赠送（全表应只有一行为1，代码不强制校验，配置时自行保证）';
