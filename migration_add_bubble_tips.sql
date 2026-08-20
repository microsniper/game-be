-- 游戏区猫咪气泡提示：bubble_tips（接口 /api/game/bubble-tips，进关拉一次整池存内存）
-- 文案全部写死在配置里（含「不到1%」这类数字），后端不做统计计算、不做占位符替换，改文案只改本行 config_value。
--
-- 两类文案共用 tips[] 数组，靠 scene 字段区分：
--   不填 scene = 随机池，按 weight 权重随机轮播
--   填了 scene = 情景提示，只在对应局面触发，不参与随机轮播
--
-- 字段说明：
--   tips[].content  气泡文案，原样展示；按 15 号字最大 190px 折行，约 12 个汉字一行，建议 20 字以内
--   tips[].weight   权重，越大越容易被抽到，缺省按 1（只对随机池有意义）
--   tips[].enabled  开关，false 后端下发前直接滤掉（想临时下掉一条不用删）
--   tips[].mode     endless=仅无限模式，daily=仅每日挑战，all/缺省=都出
--   tips[].scene    情景码，三种：
--                     basket_locked = 暂存区快满且场上还有锁定果篮  → 小手指向果篮
--                     add_tray      = 暂存区快满、果篮已全解锁、果盘还能加 → 小手指向「加果盘」
--                     clear_tray    = 暂存区快满、果篮果盘都满配      → 小手指向「清空果盘」
--                   「快满」= 暂存数量达到当前容量-1（容量 4 时满 3，容量 5 时满 4）。
--                   三种情景每关合计只触发一次，取触发那一刻局面命中的那条。
--   firstDelaySeconds  进关后多久冒第一个随机气泡
--   min/maxIntervalSeconds  两个随机气泡之间的随机间隔区间
--   displaySeconds     单个气泡停留时长
-- 上面 4 个节奏参数缺失时后端补默认值（15/20/40/4），调气泡快慢不用发版；情景提示不受间隔限制。
INSERT INTO game_config (game_type, config_key, config_value, description)
VALUES (1, 'bubble_tips', '{"firstDelaySeconds":15,"minIntervalSeconds":20,"maxIntervalSeconds":40,"displaySeconds":4,"tips":[{"content":"解锁果篮可以放更多果子哦","enabled":true,"mode":"all","scene":"basket_locked"},{"content":"加果盘可以暂时放更多果子哦","enabled":true,"mode":"all","scene":"add_tray"},{"content":"试试清空果盘吧","enabled":true,"mode":"all","scene":"clear_tray"},{"content":"每日挑战过关率只有不到1%哦","weight":2,"enabled":true,"mode":"endless"},{"content":"暂存区满了就摘不动啦，留一格保命","weight":1,"enabled":true,"mode":"all"},{"content":"同色三个进筐才算清掉哦","weight":1,"enabled":true,"mode":"all"},{"content":"卡住了别急，试试道具帮一把","weight":1,"enabled":true,"mode":"all"},{"content":"每天签到能领金币，别忘了","weight":1,"enabled":true,"mode":"all"},{"content":"今天的挑战榜还没多少人上榜呢","weight":1,"enabled":true,"mode":"daily"}]}', '游戏区猫咪气泡提示文案池（随机池 + 三条情景提示，含节奏参数）')
ON DUPLICATE KEY UPDATE config_value = VALUES(config_value);
