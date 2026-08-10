-- game_collect 收敛展示图字段：单张 url 与灰/彩两态图（gray_url/color_url）功能重复，
-- 商城/奖励池原来读 url 的地方已改成读 color_url（见 GameShopMapper.xml / GameRewardConfigMapper.xml），
-- 直接删掉 url，只保留 gray_url/color_url 两个字段
ALTER TABLE game_collect DROP COLUMN url;
